import React from 'react';
import { View, Text, StyleSheet, ViewStyle } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import { Colors, Spacing } from '../../constants/theme';
import { useColorScheme } from '../../hooks/use-color-scheme';

interface LuxuryScaffoldProps {
  title?: string;
  children: React.ReactNode;
  style?: ViewStyle;
}

export function LuxuryScaffold({ title, children, style }: LuxuryScaffoldProps) {
  const colorScheme = useColorScheme() ?? 'light';
  const colors = Colors[colorScheme];

  return (
    <SafeAreaView style={[styles.safeArea, { backgroundColor: colors.background }]}>
      {title ? (
        <View style={[styles.header, { borderBottomColor: colors.border }]}>
          <Text style={[styles.title, { color: colors.textPrimary }]}>{title}</Text>
        </View>
      ) : null}
      <View style={[styles.content, { backgroundColor: colors.background }, style]}>
        {children}
      </View>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  safeArea: {
    flex: 1,
  },
  header: {
    height: 60,
    paddingHorizontal: Spacing.lg,
    flexDirection: 'row',
    alignItems: 'center',
    borderBottomWidth: 1,
  },
  title: {
    fontSize: 20,
    fontWeight: '900',
    fontFamily: 'serif',
  },
  content: {
    flex: 1,
  },
});
