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
  TOO_EARLY: {
    icon: '⏳',
    label: 'CONTRÔLE TROP TÔT',
    color: '#f59e0b',
    bgColor: '#1a1000',
    borderColor: '#78350f',
    message: 'Le billet n’est pas encore dans sa période de validité.',
  },
  WRONG_ROUTE: {
    icon: '🚆',
    label: 'MAUVAIS TRAJET',
    color: '#ef4444',
    bgColor: '#1a0505',
    borderColor: '#7f1d1d',
    message: 'Ce billet ne correspond pas au trajet actuellement contrôlé.',
  },
  NOT_FOUND: {
    icon: '❌',
    label: 'BILLET INEXISTANT',
    color: '#ef4444',
    bgColor: '#1a0505',
    borderColor: '#7f1d1d',
    message: 'Aucun billet correspondant n’a été trouvé dans le système.',
  },
  ALREADY_CONTROLLED: {
    icon: '⚠️',
    label: 'DÉJÀ CONTRÔLÉ',
    color: '#f59e0b',
    bgColor: '#1a1000',
    borderColor: '#78350f',
    message: 'Ce billet est valide, mais il a déjà été contrôlé.',
  },
  INVALID: {
    icon: '❌',
    label: 'BILLET NON VALIDE',
    color: '#ef4444',
    bgColor: '#1a0505',
    borderColor: '#7f1d1d',
    message: 'Ce billet ne peut pas être accepté.',
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
      <Text style={styles.rowValue} numberOfLines={2}>{value || '—'}</Text>
    </View>
  );
}

function getShortId(id) {
  if (!id) return null;
  return id.substring(0, 8).toUpperCase() + '...';
}

export default function ResultScreen({ route, navigation }) {
  const {
    validation,
    result: oldResult,
    parsed = {},
    error,
    controlContext,
  } = route.params ?? {};

  // Nouveau format backend :
  // validation = { ticketId, result, reason, message }
  //
  // Ancien format :
  // result = "VALID"
  const result = validation?.result || oldResult || 'ERROR';
  const reason = validation?.reason || result;
  const message = validation?.message || error;

  // Pour choisir la bonne couleur, on utilise d’abord reason,
  // sinon result.
  const cfg =
    RESULT_CONFIG[reason] ||
    RESULT_CONFIG[result] ||
    RESULT_CONFIG.ERROR;

  const ticketId = validation?.ticketId || parsed.ticketId;
  const shortId = getShortId(ticketId);

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
        <Text style={styles.message}>
          {message || cfg.message}
        </Text>

        {/* Fiche contrôle */}
        <View style={[styles.card, { borderColor: cfg.borderColor }]}>
          <Text style={[styles.cardTitle, { color: cfg.color }]}>
            Résultat du contrôle
          </Text>

          <InfoRow label="Résultat" value={result} />
          <InfoRow label="Motif" value={reason} />

          {controlContext && (
            <InfoRow
              label="Trajet contrôlé"
              value={`${controlContext.departureStationCode} → ${controlContext.arrivalStationCode}`}
            />
          )}
        </View>

        {/* Fiche billet si on a des infos */}
        {(parsed.holderName || shortId || parsed.validFrom || parsed.validUntil || parsed.issuedAt) && (
          <View style={[styles.card, { borderColor: cfg.borderColor }]}>
            <Text style={[styles.cardTitle, { color: cfg.color }]}>
              Détails du billet
            </Text>

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
              <InfoRow label="Valide jusqu’au" value={formatInstant(parsed.validUntil)} />
            )}

            {parsed.issuedAt && (
              <InfoRow label="Émis le" value={formatInstant(parsed.issuedAt)} />
            )}
          </View>
        )}

        {/* Bouton retour scanner */}
        <TouchableOpacity
          style={[styles.button, { borderColor: cfg.color }]}
          onPress={() =>
            navigation.replace('Scanner', {
              controlContext,
            })
          }
          activeOpacity={0.8}
        >
          <Text style={[styles.buttonText, { color: cfg.color }]}>
            🔍 Scanner un autre billet
          </Text>
        </TouchableOpacity>

        {/* Changer de trajet */}
        <TouchableOpacity
          style={[styles.secondaryButton, { borderColor: cfg.borderColor }]}
          onPress={() => navigation.replace('SelectTrip')}
          activeOpacity={0.8}
        >
          <Text style={styles.secondaryButtonText}>
            🚆 Changer le trajet contrôlé
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
    maxWidth: 320,
  },

  // Fiche billet / contrôle
  card: {
    backgroundColor: 'rgba(255,255,255,0.04)',
    borderRadius: 20,
    padding: 24,
    width: '100%',
    marginBottom: 24,
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

  // Boutons
  button: {
    borderWidth: 2,
    borderRadius: 16,
    paddingVertical: 16,
    paddingHorizontal: 32,
    alignItems: 'center',
    width: '100%',
    marginTop: 8,
  },
  buttonText: {
    fontSize: 16,
    fontWeight: '700',
    letterSpacing: 0.3,
  },
  secondaryButton: {
    borderWidth: 1,
    borderRadius: 16,
    paddingVertical: 14,
    paddingHorizontal: 32,
    alignItems: 'center',
    width: '100%',
    marginTop: 14,
    backgroundColor: 'rgba(255,255,255,0.03)',
  },
  secondaryButtonText: {
    color: '#d1d5db',
    fontSize: 15,
    fontWeight: '600',
  },
});