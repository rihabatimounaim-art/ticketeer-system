import React, { useState, useRef, useCallback } from 'react';
import {
  View,
  Text,
  StyleSheet,
  TouchableOpacity,
  Alert,
  ActivityIndicator,
} from 'react-native';
import { CameraView, useCameraPermissions } from 'expo-camera';
import { useFocusEffect } from '@react-navigation/native';
import AsyncStorage from '@react-native-async-storage/async-storage';
import { validateTicket } from '../api';

/**
 * Parse le contenu d'un QR Ticketeer.
 * Format : "ticketId=UUID;holderName=Nom;validFrom=...;validUntil=...;issuedAt=...|sig=SIG"
 */
function parseQrContent(raw) {
  const payload = raw.split('|')[0]; // retirer la signature
  const parts = {};
  payload.split(';').forEach((segment) => {
    const idx = segment.indexOf('=');
    if (idx !== -1) {
      const key = segment.substring(0, idx).trim();
      const value = segment.substring(idx + 1).trim();
      parts[key] = value;
    }
  });
  return parts;
}

export default function ScannerScreen({ navigation }) {
  const [permission, requestPermission] = useCameraPermissions();
  const [scanning, setScanning] = useState(true);
  const [validating, setValidating] = useState(false);
  const lastScan = useRef(null);

  // Réactiver le scan à chaque fois qu'on revient sur cet écran
  useFocusEffect(
    useCallback(() => {
      setScanning(true);
      setValidating(false);
      lastScan.current = null;
    }, [])
  );

  const handleLogout = async () => {
    await AsyncStorage.removeItem('token');
    navigation.replace('Login');
  };

  const handleBarCodeScanned = async ({ data }) => {
    if (!scanning || validating || lastScan.current === data) return;
    lastScan.current = data;
    setScanning(false);
    setValidating(true);

    const parsed = parseQrContent(data);
    const { ticketId } = parsed;

    if (!ticketId) {
      setValidating(false);
      Alert.alert(
        'QR invalide',
        "Ce QR code ne correspond pas à un billet Ticketeer.",
        [{ text: 'Réessayer', onPress: () => setScanning(true) }]
      );
      return;
    }

    try {
      const token = await AsyncStorage.getItem('token');
      const result = await validateTicket(ticketId, token);
      navigation.navigate('Result', { result, parsed });
    } catch (e) {
      if (e.message === 'SESSION_EXPIRED') {
        await AsyncStorage.removeItem('token');
        navigation.replace('Login');
        return;
      }
      navigation.navigate('Result', { result: 'ERROR', parsed, error: e.message });
    } finally {
      setValidating(false);
    }
  };

  // Caméra non encore chargée
  if (!permission) {
    return <View style={styles.container} />;
  }

  // Permission refusée
  if (!permission.granted) {
    return (
      <View style={styles.permContainer}>
        <Text style={styles.permIcon}>📷</Text>
        <Text style={styles.permTitle}>Accès caméra requis</Text>
        <Text style={styles.permText}>
          L'accès à la caméra est nécessaire pour scanner les billets.
        </Text>
        <TouchableOpacity style={styles.permButton} onPress={requestPermission}>
          <Text style={styles.permButtonText}>Autoriser</Text>
        </TouchableOpacity>
      </View>
    );
  }

  return (
    <View style={styles.container}>
      {/* Caméra en plein écran */}
      <CameraView
        style={StyleSheet.absoluteFillObject}
        facing="back"
        barcodeScannerSettings={{ barcodeTypes: ['qr'] }}
        onBarcodeScanned={scanning && !validating ? handleBarCodeScanned : undefined}
      />

      {/* Barre du haut */}
      <View style={styles.topBar}>
        <View>
          <Text style={styles.appName}>🎫 Ticketeer Agent</Text>
          <Text style={styles.appSub}>Scanner de billets</Text>
        </View>
        <TouchableOpacity style={styles.logoutBtn} onPress={handleLogout}>
          <Text style={styles.logoutText}>Déconnexion</Text>
        </TouchableOpacity>
      </View>

      {/* Zone de scan centrale */}
      <View style={styles.scanArea}>
        <View style={styles.scanBox}>
          {/* Coins du cadre */}
          <View style={[styles.corner, styles.topLeft]} />
          <View style={[styles.corner, styles.topRight]} />
          <View style={[styles.corner, styles.bottomLeft]} />
          <View style={[styles.corner, styles.bottomRight]} />

          {/* Indicateur de validation en cours */}
          {validating && (
            <View style={styles.validatingOverlay}>
              <ActivityIndicator size="large" color="#4f46e5" />
            </View>
          )}
        </View>

        <Text style={styles.scanHint}>
          {validating
            ? 'Validation en cours...'
            : scanning
            ? 'Pointez le QR code du billet'
            : 'Lecture du billet...'}
        </Text>
      </View>

      {/* Bas de l'écran */}
      <View style={styles.bottomBar}>
        <Text style={styles.bottomText}>
          Seuls les billets Ticketeer sont acceptés
        </Text>
      </View>
    </View>
  );
}

const CORNER_SIZE = 30;
const BORDER_W = 4;
const BOX_SIZE = 260;

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#000',
  },

  // Permission
  permContainer: {
    flex: 1,
    backgroundColor: '#0f1117',
    justifyContent: 'center',
    alignItems: 'center',
    padding: 40,
  },
  permIcon: { fontSize: 56, marginBottom: 16 },
  permTitle: {
    color: '#fff',
    fontSize: 22,
    fontWeight: '700',
    marginBottom: 12,
    textAlign: 'center',
  },
  permText: {
    color: '#9ca3af',
    fontSize: 15,
    textAlign: 'center',
    lineHeight: 22,
    marginBottom: 32,
  },
  permButton: {
    backgroundColor: '#4f46e5',
    borderRadius: 14,
    paddingVertical: 14,
    paddingHorizontal: 40,
  },
  permButtonText: { color: '#fff', fontSize: 16, fontWeight: '700' },

  // Top bar
  topBar: {
    position: 'absolute',
    top: 0,
    left: 0,
    right: 0,
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    paddingTop: 56,
    paddingBottom: 20,
    paddingHorizontal: 24,
    backgroundColor: 'rgba(0,0,0,0.65)',
  },
  appName: { color: '#fff', fontSize: 18, fontWeight: '700' },
  appSub: { color: '#9ca3af', fontSize: 12, marginTop: 2 },
  logoutBtn: {
    borderWidth: 1,
    borderColor: '#ef4444',
    borderRadius: 8,
    paddingVertical: 6,
    paddingHorizontal: 14,
  },
  logoutText: { color: '#ef4444', fontSize: 13, fontWeight: '600' },

  // Zone scan
  scanArea: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    marginTop: 120,
  },
  scanBox: {
    width: BOX_SIZE,
    height: BOX_SIZE,
    position: 'relative',
    justifyContent: 'center',
    alignItems: 'center',
  },
  corner: {
    position: 'absolute',
    width: CORNER_SIZE,
    height: CORNER_SIZE,
    borderColor: '#4f46e5',
  },
  topLeft: {
    top: 0, left: 0,
    borderTopWidth: BORDER_W, borderLeftWidth: BORDER_W,
    borderTopLeftRadius: 6,
  },
  topRight: {
    top: 0, right: 0,
    borderTopWidth: BORDER_W, borderRightWidth: BORDER_W,
    borderTopRightRadius: 6,
  },
  bottomLeft: {
    bottom: 0, left: 0,
    borderBottomWidth: BORDER_W, borderLeftWidth: BORDER_W,
    borderBottomLeftRadius: 6,
  },
  bottomRight: {
    bottom: 0, right: 0,
    borderBottomWidth: BORDER_W, borderRightWidth: BORDER_W,
    borderBottomRightRadius: 6,
  },
  validatingOverlay: {
    ...StyleSheet.absoluteFillObject,
    backgroundColor: 'rgba(0,0,0,0.5)',
    justifyContent: 'center',
    alignItems: 'center',
    borderRadius: 4,
  },
  scanHint: {
    color: '#fff',
    marginTop: 28,
    fontSize: 15,
    fontWeight: '500',
    textShadowColor: 'rgba(0,0,0,0.9)',
    textShadowOffset: { width: 0, height: 1 },
    textShadowRadius: 6,
  },

  // Bottom bar
  bottomBar: {
    position: 'absolute',
    bottom: 0,
    left: 0,
    right: 0,
    paddingBottom: 40,
    paddingTop: 20,
    backgroundColor: 'rgba(0,0,0,0.65)',
    alignItems: 'center',
  },
  bottomText: {
    color: '#6b7280',
    fontSize: 13,
  },
});
