package com.fares7elsadek.syncspace.messaging.application.queries.getmessage;

import com.fares7elsadek.syncspace.messaging.api.dtos.MessageDto;
import com.fares7elsadek.syncspace.messaging.application.mapper.MessageMapper;
import com.fares7elsadek.syncspace.messaging.infrastructure.repository.MessageRepository;
import com.fares7elsadek.syncspace.shared.api.ApiResponse;
import com.fares7elsadek.syncspace.shared.cqrs.QueryHandler;
import com.fares7elsadek.syncspace.shared.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GetMessageQueryHandler implements QueryHandler<GetMessageQuery, ApiResponse<MessageDto>> {

    private final MessageMapper messageMapper;
    private final MessageRepository messageRepository;
    @Override
    public ApiResponse<MessageDto> handle(GetMessageQuery query) {
        var message = messageRepository.findById(query.id())
                .orElseThrow(() -> new NotFoundException("Message not found"));

        return ApiResponse.success("Message found",messageMapper.toMessageDto(message));
    }
}
