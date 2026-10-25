import React from 'react';
import { View, Text, TextInput, StyleSheet, TextInputProps } from 'react-native';
import { Colors, BorderRadius, Spacing } from '../../constants/theme';
import { useColorScheme } from '../../hooks/use-color-scheme';

interface LuxuryTextFieldProps extends TextInputProps {
  label: string;
  error?: string;
}

export function LuxuryTextField({ label, error, style, ...props }: LuxuryTextFieldProps) {
  const colorScheme = useColorScheme() ?? 'light';
  const colors = Colors[colorScheme];

  return (
    <View style={styles.container}>
      <Text style={[styles.label, { color: colors.gold }]}>{label}</Text>
      <TextInput
        style={[
          styles.input,
          {
            backgroundColor: colors.card,
            borderColor: error ? '#EF4444' : colors.border,
            color: colors.textPrimary,
          },
          style,
        ]}
        placeholderTextColor={colors.textSecondary}
        {...props}
      />
      {error ? <Text style={styles.error}>{error}</Text> : null}
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    marginBottom: Spacing.md,
    width: '100%',
  },
  label: {
    fontSize: 11,
    fontWeight: '950',
    letterSpacing: 1.5,
    marginBottom: Spacing.sm,
    paddingHorizontal: 4,
  },
  input: {
    height: 56,
    borderWidth: 1,
    borderRadius: BorderRadius.md,
    paddingHorizontal: Spacing.md,
    fontSize: 15,
  },
  error: {
    fontSize: 12,
    color: '#EF4444',
    marginTop: 4,
    paddingHorizontal: 4,
  },
});
