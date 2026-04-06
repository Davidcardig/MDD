import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { Observable } from 'rxjs';
import { AuthService } from './services/auth.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  title = 'front';
  isLogged$!: Observable<boolean>;

  constructor(private authService: AuthService, private router: Router) {
    this.isLogged$ = this.authService.isLogged$;
  }

  get isHome(): boolean {
    return this.router.url === '/';
  }

  get isAuthPage(): boolean {
    return this.router.url === '/login' || this.router.url === '/register';
  }
}
