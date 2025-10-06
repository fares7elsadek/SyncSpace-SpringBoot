import { Injectable } from '@angular/core';
import { environment } from '../environments/environment';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { 
  addMemberResponse, 
  ApiResponse, 
  ChannelDto, 
  CreateChannelRequest, 
  CreateServerRequest, 
  FriendshipDto, 
  inviteCode, 
  MessageDto, 
  NotificationDto, 
  PaginatedMessage, 
  RoomState, 
  RoomViewer, 
  SendMessageRequest, 
  ServerDto, 
  ServerMember, 
  UpdateUserProfileRequest, 
  UserChat, 
  UserDto, 
  VideoControlEvent
} from '../models/api.model';

@Injectable({
  providedIn: 'root'
})
export class ApiService {
  private baseUrl: string = environment.apiUrl;

  constructor(private _httpClient: HttpClient) {}

  // Server endpoints
  getServers(): Observable<ApiResponse<ServerDto[]>> {
    return this._httpClient.get<ApiResponse<ServerDto[]>>(`${this.baseUrl}/server`);
  }

  getServer(id: string): Observable<ApiResponse<ServerDto>> {
    return this._httpClient.get<ApiResponse<ServerDto>>(`${this.baseUrl}/server/${id}`);
  }

  createServer(request: CreateServerRequest): Observable<ApiResponse<ServerDto>> {
    return this._httpClient.post<ApiResponse<ServerDto>>(`${this.baseUrl}/server`, request);
  }

  updateServer(id: string, request: Partial<CreateServerRequest>): Observable<ApiResponse<ServerDto>> {
    return this._httpClient.put<ApiResponse<ServerDto>>(`${this.baseUrl}/server/${id}`, request);
  }

  deleteServer(id: string): Observable<ApiResponse<void>> {
    return this._httpClient.delete<ApiResponse<void>>(`${this.baseUrl}/server/${id}`);
  }

  getInviteCode(serverId: string): Observable<ApiResponse<inviteCode>> {
    return this._httpClient.post<ApiResponse<inviteCode>>(`${this.baseUrl}/server/${serverId}/invite`,{});
  }

  joinServer(inviteCode: string, serverId: string): Observable<ApiResponse<void>> {
    return this._httpClient.post<ApiResponse<void>>(`${this.baseUrl}/server/${serverId}/join/${inviteCode}`, {});
  }

  leaveServer(id: string): Observable<ApiResponse<void>> {
    return this._httpClient.post<ApiResponse<void>>(`${this.baseUrl}/server/${id}/leave`, {});
  }

  getServerMembers(serverId: string): Observable<ApiResponse<ServerMember[]>> {
    return this._httpClient.get<ApiResponse<ServerMember[]>>(`${this.baseUrl}/server/${serverId}/members`)
  }

  addServerMember(serverId: string,username: string): Observable<ApiResponse<void>> {
    return this._httpClient.post<ApiResponse<void>>(`${this.baseUrl}/server/${serverId}/member/${username}`, {});
  }

  getServerMember(serverId: string): Observable<ApiResponse<ServerMember>> {
    return this._httpClient.get<ApiResponse<ServerMember>>(`${this.baseUrl}/server/${serverId}/member`)
  }


  // Channel endpoints
  getChannels(serverId: string): Observable<ApiResponse<ChannelDto[]>> {
    return this._httpClient.get<ApiResponse<ChannelDto[]>>(`${this.baseUrl}/channels/server/${serverId}`);
  }

  getChannel(id: string): Observable<ApiResponse<ChannelDto>> {
    return this._httpClient.get<ApiResponse<ChannelDto>>(`${this.baseUrl}/channels/${id}`);
  }

  createChannel(request: CreateChannelRequest): Observable<ApiResponse<ChannelDto>> {
    return this._httpClient.post<ApiResponse<ChannelDto>>(`${this.baseUrl}/channels/new`, request);
  }

  updateChannel(id: string, request: Partial<CreateChannelRequest>): Observable<ApiResponse<ChannelDto>> {
    return this._httpClient.put<ApiResponse<ChannelDto>>(`${this.baseUrl}/channels/${id}`, request);
  }

  deleteChannel(channelId: string, serverId: string): Observable<ApiResponse<void>> {
    return this._httpClient.delete<ApiResponse<void>>(`${this.baseUrl}/channels/${channelId}/server/${serverId}`);
  }

  addMember(channelId: string, memberId: string): Observable<ApiResponse<addMemberResponse>> {
    return this._httpClient.post<ApiResponse<addMemberResponse>>(`${this.baseUrl}/channels/${channelId}/members/${memberId}`,{});
  }

  removeMember(channelId: string, memberId: string): Observable<ApiResponse<void>> {
    return this._httpClient.delete<ApiResponse<void>>(`${this.baseUrl}/channels/${channelId}/members/${memberId}`);
  }

  getUserChats(): Observable<ApiResponse<UserChat[]>> {
    return this._httpClient.get<ApiResponse<UserChat[]>>(`${this.baseUrl}/channels/user/chats`);
  }

  fetchRoomState(roomId: string): Observable<ApiResponse<RoomState>> {
      return this._httpClient.get<ApiResponse<RoomState>>(`${this.baseUrl}/channels/room/${roomId}`);
  }

  sendControlEvent(event: any): Observable<ApiResponse<void>> {      
      return this._httpClient.post<ApiResponse<void>>(
        `${this.baseUrl}/channels/room/control`,
        event
      );
  }

  resetRoomState(roomId: string): Observable<ApiResponse<void>> {
      return this._httpClient.post<ApiResponse<void>>(`${this.baseUrl}/channels/room/${roomId}/reset`,{});
  }

  connectToRoom(roomId: string): Observable<ApiResponse<void>> {
      return this._httpClient.post<ApiResponse<void>>(`${this.baseUrl}/channels/room/${roomId}/connect`,{});
  }

  disconnectToRoom(roomId: string): Observable<ApiResponse<void>> {
      return this._httpClient.post<ApiResponse<void>>(`${this.baseUrl}/channels/room/${roomId}/disconnect`,{});
  }

  getRoomViewers(channelId: string): Observable<ApiResponse<RoomViewer[]>>{
    return this._httpClient.get<ApiResponse<RoomViewer[]>>(`${this.baseUrl}/channels/${channelId}/viewers`);
  }

  getUserActivity(): Observable<ApiResponse<RoomState[]>>{
    return this._httpClient.get<ApiResponse<RoomState[]>>(`${this.baseUrl}/channels/user/activity`);
  }

  
  // Message endpoints
  getChannelMessages(channelId: string, size: number = 0, cursor: string = ""): Observable<ApiResponse<PaginatedMessage>> {
    const params = new HttpParams().set('size', size).set('cursor', cursor);
    return this._httpClient.get<ApiResponse<PaginatedMessage>>(`${this.baseUrl}/message/channel/${channelId}`, { params });
  }

  sendMessage(request: SendMessageRequest): Observable<ApiResponse<MessageDto>> {
    return this._httpClient.post<ApiResponse<MessageDto>>(`${this.baseUrl}/message`, request);
  }

  updateMessage(id: string, content: string): Observable<ApiResponse<MessageDto>> {
    return this._httpClient.put<ApiResponse<MessageDto>>(`${this.baseUrl}/messages/${id}`, { content });
  }

  deleteMessage(id: string): Observable<ApiResponse<void>> {
    return this._httpClient.delete<ApiResponse<void>>(`${this.baseUrl}/message/${id}`);
  }

  getMessage(id: string): Observable<ApiResponse<MessageDto>> {
    return this._httpClient.delete<ApiResponse<MessageDto>>(`${this.baseUrl}/message/${id}`);
  }

  // Friend endpoints
  getFriends(): Observable<ApiResponse<FriendshipDto[]>> {
    return this._httpClient.get<ApiResponse<FriendshipDto[]>>(`${this.baseUrl}/friends`);
  }

  getFriend(id:string): Observable<ApiResponse<FriendshipDto>> {
    return this._httpClient.get<ApiResponse<FriendshipDto>>(`${this.baseUrl}/friends/${id}`);
  }

  getPendingFriendRequests(): Observable<ApiResponse<FriendshipDto[]>> {
    return this._httpClient.get<ApiResponse<FriendshipDto[]>>(`${this.baseUrl}/friends/pending`);
  }

  sendFriendRequest(username: string): Observable<ApiResponse<void>> {
    return this._httpClient.post<ApiResponse<void>>(`${this.baseUrl}/friends/user/${username}`, {});
  }

  acceptFriendRequest(id: string): Observable<ApiResponse<void>> {
    return this._httpClient.post<ApiResponse<void>>(`${this.baseUrl}/friends/accept/${id}`, {});
  }

  rejectFriendRequest(id: string): Observable<ApiResponse<void>> {
    return this._httpClient.post<ApiResponse<void>>(`${this.baseUrl}/friends/reject/${id}`, {});
  }

  removeFriend(id: string): Observable<ApiResponse<void>> {
    return this._httpClient.delete<ApiResponse<void>>(`${this.baseUrl}/friends/user/${id}`);
  }


  // Notification endpoints
  getNotifications(page:number,size:number): Observable<ApiResponse<NotificationDto[]>> {
    return this._httpClient.get<ApiResponse<NotificationDto[]>>(`${this.baseUrl}/notification?page=${page}&size=${size}`);
  }

  getUnreadNotifications(): Observable<ApiResponse<NotificationDto[]>> {
    return this._httpClient.get<ApiResponse<NotificationDto[]>>(`${this.baseUrl}/notification/unread`);
  }

  markNotificationAsRead(id: string): Observable<ApiResponse<void>> {
    return this._httpClient.post<ApiResponse<void>>(`${this.baseUrl}/notification/${id}/read`, {});
  }

  markAllNotificationsAsRead(): Observable<ApiResponse<void>> {
    return this._httpClient.post<ApiResponse<void>>(`${this.baseUrl}/notification/read`, {});
  }

  deleteNotification(id: string): Observable<ApiResponse<void>> {
    return this._httpClient.delete<ApiResponse<void>>(`${this.baseUrl}/notification/${id}`);
  }


  // User endpoints
  getUserProfile(id: string): Observable<ApiResponse<UserDto>> {
    return this._httpClient.get<ApiResponse<UserDto>>(`${this.baseUrl}/auth/user/${id}`);
  }

  updateUserProfile(request: UpdateUserProfileRequest): Observable<ApiResponse<UserDto>> {
    return this._httpClient.put<ApiResponse<UserDto>>(`${this.baseUrl}/users/profile`, request);
  }

  searchUsers(query: string): Observable<ApiResponse<UserDto[]>> {
    const params = new HttpParams().set('q', query);
    return this._httpClient.get<ApiResponse<UserDto[]>>(`${this.baseUrl}/users/search`, { params });
  }

  // File upload
  uploadFile(file: File): Observable<ApiResponse<{ url: string }>> {
    const formData = new FormData();
    formData.append('file', file);
    return this._httpClient.post<ApiResponse<{ url: string }>>(`${this.baseUrl}/upload`, formData);
  }

  fetchVideoDetails(videoId: string): any {
    return this._httpClient.get<any>(
      `https://www.googleapis.com/youtube/v3/videos?id=${videoId}&key=${environment.youtubeApiKey}&part=snippet`
    );
  }
}
