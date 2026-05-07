# StudentAuthApp

University Student Portal authentication demo built with Android, Kotlin, and Firebase Authentication.

## Features
- Splash screen with auth gate
- Email/password login
- Student registration with validation
- Forgot password email reset
- Home dashboard with profile info
- Logout, password update, and account deletion

## Firebase Setup
1. Replace `app/google-services.json` with the real Firebase config for your project.
2. Enable **Authentication > Sign-in method > Email/Password** in Firebase Console.
3. Make sure the package name in Firebase matches `com.example.studentauthapp`.

## Screens
- `SplashActivity`
- `LoginActivity`
- `RegisterActivity`
- `ForgotPasswordActivity`
- `HomeActivity`

## Notes
- The app redirects unauthenticated users to the login flow.
- Authenticated users go directly to the dashboard.
- Password update and delete account actions use the current Firebase user.

