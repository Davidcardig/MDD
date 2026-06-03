import { AuthInterceptor } from './auth.interceptor';
import { HttpRequest, HttpHandler, HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { of, throwError } from 'rxjs';

describe('AuthInterceptor', () => {
  let interceptor: AuthInterceptor;
  let authServiceMock: any;
  let routerMock: any;
  let injectorMock: any;

  beforeEach(() => {
    authServiceMock = { getToken: jest.fn(), logout: jest.fn() };
    routerMock = { navigate: jest.fn() };
    injectorMock = { get: jest.fn().mockReturnValue(authServiceMock) };

    interceptor = new AuthInterceptor(injectorMock as any, routerMock as any);
  });

  it('ajoute le header Authorization si token présent', (done) => {
    authServiceMock.getToken.mockReturnValue('abc123');
    const req = new HttpRequest('GET', '/test');
    const handler: HttpHandler = { handle: () => of(new HttpResponse({ status: 200 })) } as any;

    interceptor.intercept(req, handler).subscribe({
      next: (res) => {
        // le handler retourne un HttpResponse ; on vérifie qu'on a bien appelé handler
        expect(res instanceof HttpResponse).toBe(true);
        done();
      }
    });
  });

  it('en cas de 401, appelle logout et navigue vers /login', (done) => {
    authServiceMock.getToken.mockReturnValue(null);
    const req = new HttpRequest('GET', '/test');
    const err = new HttpErrorResponse({ status: 401, statusText: 'Unauthorized' });
    const handler: HttpHandler = { handle: () => throwError(() => err) } as any;

    interceptor.intercept(req, handler).subscribe({
      error: (e) => {
        expect(authServiceMock.logout).toHaveBeenCalled();
        expect(routerMock.navigate).toHaveBeenCalledWith(['/login']);
        expect(e).toBe(err);
        done();
      }
    });
  });
});

