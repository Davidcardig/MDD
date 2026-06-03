import { TestBed } from '@angular/core/testing';
import { HttpTestingController, HttpClientTestingModule } from '@angular/common/http/testing';
import { AuthService } from './auth.service';
import { AuthResponse, LoginRequest, RegisterRequest } from '../models/auth.model';
import { User } from '../models/user.model';

function makeTokenWithExp(expSeconds: number): string {
  const payload = Buffer.from(JSON.stringify({ exp: expSeconds })).toString('base64');
  return `header.${payload}.sig`;
}

describe('AuthService', () => {
  let service: AuthService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({ imports: [HttpClientTestingModule] });
    service = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);
    localStorage.clear();
  });

  afterEach(() => {
    httpMock.verify();
    localStorage.clear();
  });

  it('getToken et isTokenValid quand aucun token', () => {
    expect(service.getToken()).toBeNull();
    expect(service.isTokenValid()).toBe(false);
  });

  it('isTokenValid retourne false pour token malformé', () => {
    localStorage.setItem('token', 'bad.token');
    expect(service.isTokenValid()).toBe(false);
  });

  it('isTokenValid retourne true pour token avec expiration future', () => {
    const future = Math.floor(Date.now() / 1000) + 3600;
    const token = makeTokenWithExp(future);
    localStorage.setItem('token', token);
    expect(service.isTokenValid()).toBe(true);
  });

  it('login stocke le token et met à jour currentUser/isLogged', (done) => {
    const mockResp: AuthResponse = { token: 't', type: 'Bearer', id: 1, email: 'a@b', username: 'd' };

    service.login({ emailOrUsername: 'u', password: 'p' } as LoginRequest).subscribe((resp) => {
      expect(resp).toEqual(mockResp);
      expect(service.getToken()).toBe(mockResp.token);
      service.currentUser$.subscribe(user => {
        expect(user).toBeTruthy();
        expect(user?.username).toBe('d');
        service.isLogged$.subscribe(is => {
          expect(is).toBe(true);
          done();
        });
      });
    });

    const req = httpMock.expectOne('http://localhost:8080/api/auth/login');
    expect(req.request.method).toBe('POST');
    req.flush(mockResp);
  });

  it('register stocke le token et met à jour currentUser/isLogged', (done) => {
    const mockResp: AuthResponse = { token: 'rt', type: 'Bearer', id: 2, email: 'x@y', username: 'u2' };
    service.register({ email: 'x@y', username: 'u2', password: 'p' } as RegisterRequest).subscribe(resp => {
      expect(resp).toEqual(mockResp);
      expect(service.getToken()).toBe(mockResp.token);
      service.currentUser$.subscribe(user => {
        expect(user?.username).toBe('u2');
        service.isLogged$.subscribe(is => {
          expect(is).toBe(true);
          done();
        });
      });
    });

    const req = httpMock.expectOne('http://localhost:8080/api/auth/register');
    expect(req.request.method).toBe('POST');
    req.flush(mockResp);
  });

  it('init ne fait rien si pas de token', async () => {
    const res = await service.init();
    expect(res).toBeUndefined();
  });

  it('init charge l utilisateur si token présent', async () => {
    const future = Math.floor(Date.now() / 1000) + 3600;
    const token = makeTokenWithExp(future);
    localStorage.setItem('token', token);

    const user: User = { id: 5, username: 'u5', email: 'u5@x' };
    const p = service.init();
    const req = httpMock.expectOne('http://localhost:8080/api/user/me');
    expect(req.request.method).toBe('GET');
    req.flush(user);
    await p;

    service.currentUser$.subscribe(u => {
      expect(u?.id).toBe(5);
    });
  });

  it('init supprime le token si le serveur renvoie 401', async () => {
    const future = Math.floor(Date.now() / 1000) + 3600;
    const token = makeTokenWithExp(future);
    localStorage.setItem('token', token);

    const p = service.init();
    const req = httpMock.expectOne('http://localhost:8080/api/user/me');
    req.flush({}, { status: 401, statusText: 'Unauthorized' });
    await p;

    expect(service.getToken()).toBeNull();
    service.isLogged$.subscribe(is => expect(is).toBe(false));
  });

  it('logout supprime token et met isLogged à false', () => {
    localStorage.setItem('token', 't');
    service.logout();
    expect(service.getToken()).toBeNull();
    service.isLogged$.subscribe(is => expect(is).toBe(false));
  });
});


