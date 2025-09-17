package com.fares7elsadek.syncspace.channel.application.commands.addmember;

import com.fares7elsadek.syncspace.channel.api.dtos.ChannelChatUserDto;
import com.fares7elsadek.syncspace.channel.api.dtos.ChannelDto;

public record AddMemberResponse(ChannelDto channel, ChannelChatUserDto user) {
}
