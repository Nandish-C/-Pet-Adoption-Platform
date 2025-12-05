import { HttpInterceptorFn } from '@angular/common/http';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  console.log('Auth Interceptor - Request URL:', req.url);
  console.log('Auth Interceptor - Request method:', req.method);
  const token = localStorage.getItem('authToken');
  console.log('Auth Interceptor - Token exists:', !!token);
  console.log('Auth Interceptor - Token length:', token ? token.length : 0);

  // Skip interceptor for login request
  if (req.url.includes('/api/auth/login')) {
    console.log('Auth Interceptor - Skipping login request');
    return next(req);
  }

  if (token) {
    console.log('Auth Interceptor - Adding token to request');
    const authReq = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    });
    console.log('Auth Interceptor - Headers set:', authReq.headers.has('Authorization'));
    console.log('Auth Interceptor - Authorization header value:', authReq.headers.get('Authorization'));
    return next(authReq);
  }

  console.log('Auth Interceptor - No token, proceeding without auth header');
  return next(req);
};
