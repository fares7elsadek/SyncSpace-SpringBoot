package com.fares7elsadek.syncspace.channel.queries.listchat;

import com.fares7elsadek.syncspace.channel.model.dtos.ChannelChatDto;
import com.fares7elsadek.syncspace.channel.model.dtos.ChannelDto;
import com.fares7elsadek.syncspace.shared.api.ApiResponse;
import com.fares7elsadek.syncspace.shared.cqrs.Query;

import java.util.List;

public record ListChatsQuery() implements Query<ApiResponse<List<ChannelChatDto>>> {
}
