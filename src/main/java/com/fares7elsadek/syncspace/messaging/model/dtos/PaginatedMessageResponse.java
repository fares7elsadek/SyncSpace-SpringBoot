package com.fares7elsadek.syncspace.messaging.model.dtos;

import java.util.List;

public record PaginatedMessageResponse(String nextCursor, boolean hasMore, List<MessageDto> messages) {
}
