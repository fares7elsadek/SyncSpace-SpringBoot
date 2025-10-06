import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { ApiResponse, UserDto } from '../models/api.model';
import { KeycloakService } from 'keycloak-angular';
import { HttpClient } from '@angular/common/http';
import { environment } from '../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private currentUserSubject = new BehaviorSubject<UserDto | null>(null);
  public currentUser$ = this.currentUserSubject.asObservable();

  constructor(private kc:KeycloakService,private http:HttpClient){}

  async init(): Promise<boolean>{
    return this.kc.init({
      config: {
        url: environment.keycloak.url,
        realm: environment.keycloak.realm,
        clientId: environment.keycloak.clientId
      },
      initOptions: {
        onLoad: 'login-required', 
        silentCheckSsoRedirectUri: window.location.origin + '/silent-check-sso.html',
        pkceMethod: 'S256',
      },
       loadUserProfileAtStartUp: false
    });
  }

  async initAuth(): Promise<boolean>{
    try{
      const authenticated = await this.kc.isLoggedIn();
      if(authenticated)
        this.loadUserInfo();
      return authenticated;
    }catch(error){
      console.error('Auth initialization failed:', error);
      return false;
    }
  }

  async login(): Promise<void>{
    await this.kc.login({
      redirectUri: window.location.origin
    })
  }

  async logout():Promise<void>{
    this.currentUserSubject.next(null);
    await this.kc.logout()
  }
  
  async loadUserInfo(): Promise<void>{
    try {
      await this.http.get<ApiResponse<UserDto>>(`${environment.apiUrl}/auth/userData`).subscribe({
        next:(respone)=>{
          this.currentUserSubject.next(respone.data);
        }
      });
    } catch (error) {
      console.error('Failed to load user info:', error);
    }
  }

  getToken(): Promise<string> {
    return this.kc.getToken();
  }

  getCurrentUser(): UserDto | null {
    return this.currentUserSubject.value;
  }

  async isLoggedIn(): Promise<boolean> {
    return await this.kc.isLoggedIn();
  }

  hasRole(role: string): boolean {
    return this.kc.isUserInRole(role);
  }

}
