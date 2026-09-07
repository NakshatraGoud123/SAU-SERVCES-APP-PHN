# Implementation Plan - Remove Phone Number Login

Remove phone/mobile number authentication and keep only email/password login across both standard and luxury UI themes.

## Proposed Changes

### [Authentication UI - Luxury Theme]

#### [MODIFY] [LuxuryAuth.kt](file:///E:/ANDROID-PROJECTS/app/src/main/java/com/nisr/sauservices/ui/luxury/LuxuryAuth.kt)
- In `LuxuryLoginScreen`, replace `mobile` state and text field with `email`. Update label to "EMAIL ADDRESS" and icon to `Icons.Default.Email`.
- In `LuxurySignUpScreen`, remove `mobile` state and text field.
- In `LuxuryForgotPasswordScreen`, replace `mobile` state and text field with `email`. Update instructions to mention reset email.

### [Authentication UI - Standard Theme]

#### [MODIFY] [SignInScreen.kt](file:///E:/ANDROID-PROJECTS/app/src/main/java/com/nisr/sauservices/ui/auth/SignInScreen.kt)
- Replace phone number input section with Email and Password fields.
- Update `PROCEED` button to perform email sign-in using `AuthViewModel.signIn`.
- Remove Google login button if the goal is strictly email/password only (as per "keep ONLY email and password login").
  - *Decision*: I will remove Google login to adhere strictly to the request.

#### [MODIFY] [SignUpScreen.kt](file:///E:/ANDROID-PROJECTS/app/src/main/java/com/nisr/sauservices/ui/auth/SignUpScreen.kt)
- Remove `phoneNumber` state and the corresponding `LuxuryTextField`.
- Update `CREATE ACCOUNT` button logic to remove `phone_number` from `userData` map.
- Remove Google signup button.

#### [DELETE] [PhoneLoginScreen.kt](file:///E:/ANDROID-PROJECTS/app/src/main/java/com/nisr/sauservices/ui/auth/PhoneLoginScreen.kt)
- This screen is for phone OTP verification and is no longer needed.

### [Data & Logic]

#### [MODIFY] [AuthViewModel.kt](file:///E:/ANDROID-PROJECTS/app/src/main/java/com/nisr/sauservices/ui/viewmodel/AuthViewModel.kt)
- Remove `sendOtp` and `verifyOtp` functions.
- Remove `signInWithGoogle` if social login is being removed.

### [Navigation]

#### [MODIFY] [AuthNavGraph.kt](file:///E:/ANDROID-PROJECTS/app/src/main/java/com/nisr/sauservices/navigation/AuthNavGraph.kt)
- Update `Screen.AuthOptions` to use the modified `SignInScreen` (which will now be email-based).
- Remove `Screen.Otp` destination if it's no longer used.

## Verification Plan

### Manual Verification
- Deploy the app and verify the Login screen (both standard and luxury) shows Email/Password fields only.
- Test Sign Up flow without phone number.
- Test Forgot Password flow with email.
- Verify that social login (Google) and Phone Number options are no longer visible.
