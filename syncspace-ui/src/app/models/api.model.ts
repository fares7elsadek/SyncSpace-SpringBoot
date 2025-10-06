export interface UserDto {
  id: string;
  username: string;
  email: string;
  firstName: string;
  lastName: string;
  displayName?:string;
  avatarUrl?: string;
  isOnline: boolean;
  lastSeen: string;
  createdAt: string;
  updatedAt: string;
}

export interface ServerDto {
  id: string;
  name: string;
  description?: string;
  iconUrl?: string;
  createdAt: string;
  updatedAt: string;
  membersNumber: number
}

export interface ChannelDto {
  id: string;
  name: string;
  description?: string;
  isPrivate:boolean;
  isGroup:boolean;
  type: "TEXT" | "STREAMING"
}

export interface MessageDto {
  messageId: string;
  content: string;
  sender: UserDto;
  channelId: string;
  attachmentUrls?: string[];
  sentAt: string;
}

export interface FriendshipDto {
  id: string;
  user:UserDto
  createdAt:string,
  friendShipStatus: 'PENDING' | 'ACCEPTED' | 'REJECTED'
}

export interface NotificationDto {
  id: string;
  type: 'DIRECT_MESSAGE' | 'FRIEND_REQUEST' | 'FRIEND_ACCEPTED' | 'SYSTEM' | "GROUP_MESSAGE";
  title: string;
  content: string;
  relatedEntityId: string;
  createdAt:string
  read:boolean
}

// WebSocket Models
export interface PresenceMessage {
  userId: string;
  status: 'ONLINE' | 'OFFLINE';
}

export interface TypingMessage {
  userId: string;
  channelId: string;
  isTyping: boolean;
  timestamp: string;
}

export interface MessageDeletedEvent {
  messageId: string;
  channelId: string;
  deletedBy: string;
  timestamp: string;
}

// Request/Response Models
export interface CreateServerRequest {
  name: string;
  description?: string;
  isPublic:boolean;
}

export interface CreateChannelRequest {
  name: string;
  description?: string;
  isPrivate: boolean;
  serverId: string;
  type: "TEXT" | "STREAMING"
}

export interface SendMessageRequest {
  content: string;
  channelId: string;
  attachmentsIds?: string[];
  messageType: string
}

export interface FriendRequestRequest {
  friendUsername: string;
}

export interface UpdateUserProfileRequest {
  displayName?: string;
  avatarUrl?: string;
}

export interface inviteCode{
  id:string;
  serverId:string;
  serverName:string;
  code:string;
  maxUses:number;
  uses:number;
  expiresAt:string;
}

export interface addMemberResponse{
  user:UserDto,
  channel:ChannelDto
}

export interface ChannelChat{
  id: string;
  name: string;
  description?: string;
  isPrivate:boolean;
  isGroup:boolean;
  lastReadMessage:MessageDto,
  unReadMessages:number
}

export interface UserChat{
  user:UserDto,
  channel:ChannelChat
}

export interface PaginatedMessage{
    nextCursor:string,
    hasMore:boolean,
    messages: MessageDto[];
}

export interface ApiResponse<T>{
    success:boolean,
    message:string,
    data:T
}

export interface ServerMember{
  role:string,
  nickname:string,
  serverUserDto:UserDto
}

export interface RoomState {
  roomId: string;
  videoUrl: string;
  currentTimestamp: number;
  isPlaying: boolean;
  lastUpdatedAt: string;
  playbackRate: number;
  channel?:ChannelDto;
  hoster?:UserDto;
  videoTitle?:string;
  thumbnail?:string;
  viewers?:RoomViewer[];
}

export interface VideoControlEvent {
  channelId: string;
  action: 'PLAY' | 'PAUSE' | 'SEEK' | 'CHANGE_VIDEO';
  timestamp?: number;
  videoUrl?: string;
  user: UserDto;
}

export interface RoomViewer {
  id:string;
  user:UserDto;
  connectedAt:string;
}