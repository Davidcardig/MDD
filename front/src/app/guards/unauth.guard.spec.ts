import { TestBed } from '@angular/core/testing';
import { UnauthGuard } from './unauth.guard';
import { AuthService } from '../services/auth.service';
import { Router } from '@angular/router';

describe('UnauthGuard', () => {
  let guard: UnauthGuard;
  let authServiceMock: any;
  let routerMock: any;

  beforeEach(() => {
    authServiceMock = { isTokenValid: jest.fn() };
    routerMock = { createUrlTree: jest.fn().mockReturnValue('/home') };

    TestBed.configureTestingModule({
      providers: [
        UnauthGuard,
        { provide: AuthService, useValue: authServiceMock },
        { provide: Router, useValue: routerMock }
      ]
    });

    guard = TestBed.inject(UnauthGuard);
  });

  it('devrait permettre l accès si le token n est pas valide', () => {
    authServiceMock.isTokenValid.mockReturnValue(false);
    const result = guard.canActivate(null as any, null as any);
    expect(result).toBe(true);
  });

  it('devrait rediriger si le token est valide', () => {
    authServiceMock.isTokenValid.mockReturnValue(true);
    const result = guard.canActivate(null as any, null as any);
    expect(routerMock.createUrlTree).toHaveBeenCalledWith(['/']);
    expect(result).toBe(routerMock.createUrlTree());
  });
});

