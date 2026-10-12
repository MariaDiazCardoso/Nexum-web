import { HttpInterceptorFn } from '@angular/common/http';

const TOKEN_STORAGE_KEY = 'nexum.accessToken';

export const authInterceptor: HttpInterceptorFn = (request, next) => {
  const token = globalThis.sessionStorage?.getItem(TOKEN_STORAGE_KEY);

  if (!token) {
    return next(request);
  }

  return next(
    request.clone({
      setHeaders: { Authorization: `Bearer ${token}` },
    }),
  );
};
