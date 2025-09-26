package com.fares7elsadek.syncspace.messaging.application.queries.getmessages;

import com.fares7elsadek.syncspace.channel.shared.ChannelAccessService;
import com.fares7elsadek.syncspace.messaging.api.dtos.MessageDto;
import com.fares7elsadek.syncspace.messaging.api.dtos.PaginatedMessageResponse;
import com.fares7elsadek.syncspace.messaging.application.mapper.MessageMapper;
import com.fares7elsadek.syncspace.messaging.domain.model.Message;
import com.fares7elsadek.syncspace.messaging.infrastructure.repository.MessageRepository;
import com.fares7elsadek.syncspace.shared.api.ApiResponse;
import com.fares7elsadek.syncspace.shared.cqrs.QueryHandler;
import com.fares7elsadek.syncspace.user.shared.UserAccessService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class GetMessagesQueryHandler
        implements QueryHandler<GetMessagesQuery, ApiResponse<PaginatedMessageResponse>> {

    private final MessageRepository messageRepository;
    private final MessageMapper messageMapper;
    private final ChannelAccessService channelAccessService;
    private final UserAccessService userAccessService;

    @Override
    public ApiResponse<PaginatedMessageResponse> handle(GetMessagesQuery query) {
        LocalDateTime cursor = validCursor(query) ? decodeCursor(query.cursor()) : null;
        String channelId = query.channelId();
        int size = query.size();

        var user = userAccessService.getCurrentUserInfo();
        channelAccessService.getChannelMembers(channelId, user.getId());

        Pageable pageable = PageRequest.of(0, size + 1);
        List<Message> messages = messageRepository.findMessagesByChannelIdWithCursor(channelId, cursor, pageable);

        boolean hasMore = messages.size() > size;
        if (hasMore) {
            messages = messages.subList(0, size);
        }

        if (messages.isEmpty()) {
            return ApiResponse.success("Messages", new PaginatedMessageResponse(null, false, List.of()));
        }

        Collections.reverse(messages);

        String nextCursor = encodeCursor(messages.get(messages.size() - 1).getCreatedAt());

        List<MessageDto> dtos = messages.stream()
                .map(messageMapper::toMessageDto)
                .toList();

        if(!validCursor(query)){
            channelAccessService
                    .updateChannelReadState(channelAccessService.getChannel(channelId),
                            user,messages.getLast());
        }


        var dto = new PaginatedMessageResponse(nextCursor, hasMore, dtos);

        return ApiResponse.success("Messages", dto);
    }

    private LocalDateTime decodeCursor(String cursor) {
        String decoded = new String(Base64.getDecoder().decode(cursor), StandardCharsets.UTF_8);
        return LocalDateTime.parse(decoded);
    }

    private String encodeCursor(LocalDateTime cursor) {
        return Base64.getEncoder().encodeToString(cursor.toString().getBytes(StandardCharsets.UTF_8));
    }

    private boolean validCursor(GetMessagesQuery query){
        return query.cursor() != null && !query.cursor().isBlank();
    }
}
