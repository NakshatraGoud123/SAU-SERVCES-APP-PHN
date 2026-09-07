# Migrate Google Sign-In to Credential Manager API

This plan migrates the legacy Google Sign-In API to the modern Credential Manager API to resolve the generic "Error 10" (DEVELOPER_ERROR) and ensure long-term compatibility.

## Proposed Changes

### [MODIFY] [GoogleSignInUtils.kt](file:///E:/ANDROID-PROJECTS/app/src/main/java/com/nisr/sauservices/ui/auth/GoogleSignInUtils.kt)
Rewrite `GoogleSignInUtils` to use `CredentialManager` and `GetGoogleIdOption`. This will now be a suspending function that returns the ID Token directly or throws an exception.

### [MODIFY] [LoginScreen.kt](file:///E:/ANDROID-PROJECTS/app/src/main/java/com/nisr/sauservices/ui/auth/LoginScreen.kt)
- Remove `rememberLauncherForActivityResult`.
- Update the Google button `onClick` to launch a coroutine and call `GoogleSignInUtils.launchGoogleSignIn`.
- Directly call `authViewModel.signInWithGoogle(idToken)`.

### [MODIFY] [SignInScreen.kt](file:///E:/ANDROID-PROJECTS/app/src/main/java/com/nisr/sauservices/ui/auth/SignInScreen.kt)
- Remove `rememberLauncherForActivityResult`.
- Update the Google button `onClick` to launch a coroutine and call `GoogleSignInUtils.launchGoogleSignIn`.
- Directly call `authViewModel.signInWithGoogle(idToken)`.

## Verification Plan

### Manual Verification
- Deploy the app to a physical device or emulator with Play Services.
- Tap the "Sign in with Google" button.
- Verify the Google account picker appears.
- Verify that selecting an account successfully signs the user in via Supabase (if the Web Client ID and SHA-1 are correctly configured in the console).
