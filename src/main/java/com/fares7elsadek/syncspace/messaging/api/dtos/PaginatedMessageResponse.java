package com.fares7elsadek.syncspace.messaging.api.dtos;

import java.util.List;

public record PaginatedMessageResponse(String nextCursor, boolean hasMore, List<MessageDto> messages) {
}
