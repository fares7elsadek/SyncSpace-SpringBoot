import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, Router } from '@angular/router';
import { KeycloakService } from 'keycloak-angular';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {
  constructor(
    private keycloakService: KeycloakService,
    private router: Router
  ) {}

  async canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): Promise<boolean> {
    try {
      if (!this.keycloakService.getKeycloakInstance()) {
        console.error('Keycloak not initialized');
        return false;
      }

      const isLoggedIn = await this.keycloakService.isLoggedIn();

      if (!isLoggedIn) {
        await this.keycloakService.login({
          redirectUri: window.location.origin + state.url,
        });
        return false;
      }

      return true;
    } catch (error) {
      console.error('Auth guard error:', error);
      return false;
    }
  }
}