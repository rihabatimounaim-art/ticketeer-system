import React from 'react';
import {
  View,
  Text,
  StyleSheet,
  TouchableOpacity,
  ScrollView,
  SafeAreaView,
} from 'react-native';

// Configuration visuelle par résultat
const RESULT_CONFIG = {
  VALID: {
    icon: '✅',
    label: 'BILLET VALIDE',
    color: '#22c55e',
    bgColor: '#0a1f0e',
    borderColor: '#166534',
    message: 'Ce billet est authentique et valide. Le passager peut embarquer.',
  },
  EXPIRED: {
    icon: '⏰',
    label: 'BILLET EXPIRÉ',
    color: '#f59e0b',
    bgColor: '#1a1000',
    borderColor: '#78350f',
    message: 'La période de validité de ce billet est dépassée.',
  },
  ALREADY_CONTROLLED: {
    icon: '🚫',
    label: 'DÉJÀ CONTRÔLÉ',
    color: '#ef4444',
    bgColor: '#1a0505',
    borderColor: '#7f1d1d',
    message: 'Ce billet a déjà été scanné lors d\'un précédent contrôle.',
  },
  ERROR: {
    icon: '❌',
    label: 'ERREUR',
    color: '#ef4444',
    bgColor: '#1a0505',
    borderColor: '#7f1d1d',
    message: 'Impossible de vérifier ce billet. Vérifiez la connexion.',
  },
};

function formatInstant(iso) {
  if (!iso) return '—';
  try {
    return new Date(iso).toLocaleString('fr-FR', {
      day: '2-digit',
      month: '2-digit',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
    });
  } catch {
    return iso;
  }
}

function InfoRow({ label, value }) {
  return (
    <View style={styles.row}>
      <Text style={styles.rowLabel}>{label}</Text>
      <Text style={styles.rowValue} numberOfLines={2}>{value}</Text>
    </View>
  );
}

export default function ResultScreen({ route, navigation }) {
  const { result, parsed = {}, error } = route.params ?? {};
  const cfg = RESULT_CONFIG[result] ?? RESULT_CONFIG.ERROR;

  const shortId = parsed.ticketId
    ? parsed.ticketId.substring(0, 8).toUpperCase() + '...'
    : null;

  return (
    <SafeAreaView style={[styles.container, { backgroundColor: cfg.bgColor }]}>
      <ScrollView
        contentContainerStyle={styles.inner}
        showsVerticalScrollIndicator={false}
      >
        {/* Icône résultat */}
        <Text style={styles.icon}>{cfg.icon}</Text>

        {/* Label résultat */}
        <Text style={[styles.label, { color: cfg.color }]}>{cfg.label}</Text>

        {/* Message */}
        <Text style={styles.message}>{error || cfg.message}</Text>

        {/* Fiche billet si on a des infos */}
        {(parsed.holderName || parsed.ticketId) && (
          <View style={[styles.card, { borderColor: cfg.borderColor }]}>
            <Text style={[styles.cardTitle, { color: cfg.color }]}>Détails du billet</Text>

            {parsed.holderName && (
              <InfoRow label="Passager" value={parsed.holderName} />
            )}
            {shortId && (
              <InfoRow label="N° billet" value={shortId} />
            )}
            {parsed.validFrom && (
              <InfoRow label="Valide du" value={formatInstant(parsed.validFrom)} />
            )}
            {parsed.validUntil && (
              <InfoRow label="Valide jusqu'au" value={formatInstant(parsed.validUntil)} />
            )}
            {parsed.issuedAt && (
              <InfoRow label="Émis le" value={formatInstant(parsed.issuedAt)} />
            )}
          </View>
        )}

        {/* Bouton retour scanner */}
        <TouchableOpacity
          style={[styles.button, { borderColor: cfg.color }]}
          onPress={() => navigation.goBack()}
          activeOpacity={0.8}
        >
          <Text style={[styles.buttonText, { color: cfg.color }]}>
            🔍 Scanner un autre billet
          </Text>
        </TouchableOpacity>
      </ScrollView>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
  },
  inner: {
    flexGrow: 1,
    justifyContent: 'center',
    alignItems: 'center',
    padding: 32,
    paddingTop: 48,
    paddingBottom: 48,
  },
  icon: {
    fontSize: 84,
    marginBottom: 16,
  },
  label: {
    fontSize: 24,
    fontWeight: '800',
    letterSpacing: 1.5,
    textAlign: 'center',
    marginBottom: 12,
  },
  message: {
    color: '#9ca3af',
    fontSize: 15,
    textAlign: 'center',
    lineHeight: 22,
    marginBottom: 32,
    maxWidth: 300,
  },

  // Fiche billet
  card: {
    backgroundColor: 'rgba(255,255,255,0.04)',
    borderRadius: 20,
    padding: 24,
    width: '100%',
    marginBottom: 32,
    borderWidth: 1,
  },
  cardTitle: {
    fontSize: 12,
    fontWeight: '700',
    textTransform: 'uppercase',
    letterSpacing: 1,
    marginBottom: 16,
  },
  row: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'flex-start',
    paddingVertical: 10,
    borderBottomWidth: 1,
    borderBottomColor: 'rgba(255,255,255,0.06)',
  },
  rowLabel: {
    color: '#6b7280',
    fontSize: 14,
    flex: 1,
  },
  rowValue: {
    color: '#f3f4f6',
    fontSize: 14,
    fontWeight: '500',
    flex: 2,
    textAlign: 'right',
  },

  // Bouton
  button: {
    borderWidth: 2,
    borderRadius: 16,
    paddingVertical: 16,
    paddingHorizontal: 32,
    alignItems: 'center',
    width: '100%',
  },
  buttonText: {
    fontSize: 16,
    fontWeight: '700',
    letterSpacing: 0.3,
  },
});
