import React, { useEffect, useState } from 'react';
import {
  View,
  Text,
  TouchableOpacity,
  StyleSheet,
  Alert,
  ActivityIndicator,
  ScrollView,
} from 'react-native';
import { Picker } from '@react-native-picker/picker';
import AsyncStorage from '@react-native-async-storage/async-storage';
import DateTimePicker from '@react-native-community/datetimepicker';
import { getStations, searchTrips } from '../api';

function formatDateTime(value) {
  if (!value) return '—';

  try {
    return new Date(value).toLocaleString('fr-FR', {
      day: '2-digit',
      month: '2-digit',
      hour: '2-digit',
      minute: '2-digit',
    });
  } catch {
    return value;
  }
}

function formatTripLabel(trip) {
  const departure = trip.departureTime || trip.departure_time;
  const arrival = trip.arrivalTime || trip.arrival_time;

  return `${formatDateTime(departure)} → ${formatDateTime(arrival)}`;
}

export default function SelectTripScreen({ navigation }) {
  const today = new Date().toISOString().slice(0, 10);

  const [stations, setStations] = useState([]);
  const [trips, setTrips] = useState([]);

  const [departureStationCode, setDepartureStationCode] = useState('');
  const [arrivalStationCode, setArrivalStationCode] = useState('');
  const [selectedDateObj, setSelectedDateObj] = useState(new Date());
const [showDatePicker, setShowDatePicker] = useState(false);

const selectedDate = selectedDateObj.toISOString().slice(0, 10);
  const [selectedTripId, setSelectedTripId] = useState('');

  const [loadingStations, setLoadingStations] = useState(true);
  const [loadingTrips, setLoadingTrips] = useState(false);

  useEffect(() => {
    loadStations();
  }, []);

  useEffect(() => {
    if (
      departureStationCode &&
      arrivalStationCode &&
      selectedDate &&
      departureStationCode !== arrivalStationCode
    ) {
      loadTrips();
    } else {
      setTrips([]);
      setSelectedTripId('');
    }
  }, [departureStationCode, arrivalStationCode, selectedDate]);

  const loadStations = async () => {
    try {
      setLoadingStations(true);

      const data = await getStations();
      setStations(data);

      if (data.length >= 2) {
        setDepartureStationCode(data[0].code);
        setArrivalStationCode(data[1].code);
      }
    } catch (error) {
      Alert.alert('Erreur', error.message);
    } finally {
      setLoadingStations(false);
    }
  };

  const loadTrips = async () => {
    try {
      setLoadingTrips(true);
      setSelectedTripId('');

      const data = await searchTrips(
        departureStationCode,
        arrivalStationCode,
        selectedDate
      );

      setTrips(data);

      if (data.length > 0) {
        setSelectedTripId(data[0].id);
      }
    } catch (error) {
      setTrips([]);
      Alert.alert('Erreur', error.message);
    } finally {
      setLoadingTrips(false);
    }
  };

  const handleLogout = async () => {
    await AsyncStorage.removeItem('token');
    navigation.replace('Login');
  };

  const handleContinue = () => {
    if (!departureStationCode || !arrivalStationCode) {
      Alert.alert(
        'Champs requis',
        'Veuillez sélectionner une station de départ et une station d’arrivée.'
      );
      return;
    }

    if (!selectedDate) {
      Alert.alert('Date requise', 'Veuillez saisir une date de trajet.');
      return;
    }

    if (departureStationCode === arrivalStationCode) {
      Alert.alert(
        'Trajet invalide',
        'La station de départ et la station d’arrivée doivent être différentes.'
      );
      return;
    }

    if (!selectedTripId) {
      Alert.alert('Trajet requis', 'Veuillez sélectionner un horaire de trajet.');
      return;
    }

    const selectedTrip = trips.find(
      (trip) => String(trip.id) === String(selectedTripId)
    );

    if (!selectedTrip) {
      Alert.alert('Erreur', 'Le trajet sélectionné est introuvable.');
      return;
    }

    navigation.replace('Scanner', {
      controlContext: {
        tripId: selectedTrip.id,
        departureStationCode,
        arrivalStationCode,
        date: selectedDate,
        departureTime: selectedTrip.departureTime || selectedTrip.departure_time,
        arrivalTime: selectedTrip.arrivalTime || selectedTrip.arrival_time,
      },
    });
  };

  if (loadingStations) {
    return (
      <View style={styles.container}>
        <ActivityIndicator color="#4f46e5" size="large" />
        <Text style={styles.loadingText}>Chargement des stations...</Text>
      </View>
    );
  }

  return (
    <ScrollView contentContainerStyle={styles.scrollContainer}>
      <View style={styles.topBar}>
        <View>
          <Text style={styles.appName}>🎫 Ticketeer Agent</Text>
          <Text style={styles.appSub}>Choix du trajet contrôlé</Text>
        </View>

        <TouchableOpacity style={styles.logoutBtn} onPress={handleLogout}>
          <Text style={styles.logoutText}>Déconnexion</Text>
        </TouchableOpacity>
      </View>

      <Text style={styles.title}>Trajet contrôlé</Text>
      <Text style={styles.subtitle}>
        Sélectionnez le trajet sur lequel l’agent effectue le contrôle.
      </Text>

      <View style={styles.form}>
        <Text style={styles.label}>Station de départ</Text>
        <View style={styles.pickerWrapper}>
          <Picker
            selectedValue={departureStationCode}
            onValueChange={(value) => setDepartureStationCode(value)}
            dropdownIconColor="#ffffff"
            style={styles.picker}
          >
            {stations.map((station) => (
              <Picker.Item
                key={station.code}
                label={`${station.name} (${station.code})`}
                value={station.code}
              />
            ))}
          </Picker>
        </View>

        <Text style={styles.label}>Station d’arrivée</Text>
        <View style={styles.pickerWrapper}>
          <Picker
            selectedValue={arrivalStationCode}
            onValueChange={(value) => setArrivalStationCode(value)}
            dropdownIconColor="#ffffff"
            style={styles.picker}
          >
            {stations.map((station) => (
              <Picker.Item
                key={station.code}
                label={`${station.name} (${station.code})`}
                value={station.code}
              />
            ))}
          </Picker>
        </View>

        <Text style={styles.label}>Date du trajet</Text>

<TouchableOpacity
  style={styles.input}
  onPress={() => setShowDatePicker(true)}
>
  <Text style={styles.dateText}>{selectedDate}</Text>
</TouchableOpacity>

{showDatePicker && (
  <DateTimePicker
    value={selectedDateObj}
    mode="date"
    display="default"
    onChange={(event, date) => {
      setShowDatePicker(false);

      if (date) {
        setSelectedDateObj(date);
      }
    }}
  />
)}

        <Text style={styles.label}>Horaire / trajet disponible</Text>

        {loadingTrips ? (
          <View style={styles.loadingBox}>
            <ActivityIndicator color="#4f46e5" />
            <Text style={styles.loadingTextSmall}>
              Chargement des trajets...
            </Text>
          </View>
        ) : trips.length === 0 ? (
          <Text style={styles.noTripText}>
            Aucun trajet disponible pour cette sélection.
          </Text>
        ) : (
          <View style={styles.pickerWrapper}>
            <Picker
              selectedValue={selectedTripId}
              onValueChange={(value) => setSelectedTripId(value)}
              dropdownIconColor="#ffffff"
              style={styles.picker}
            >
              {trips.map((trip) => (
                <Picker.Item
                  key={trip.id}
                  label={formatTripLabel(trip)}
                  value={trip.id}
                />
              ))}
            </Picker>
          </View>
        )}

        <TouchableOpacity style={styles.button} onPress={handleContinue}>
          <Text style={styles.buttonText}>Commencer le contrôle</Text>
        </TouchableOpacity>
      </View>

      <Text style={styles.help}>
        Le billet scanné sera comparé au trajet, à la date et à l’horaire sélectionnés.
      </Text>
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  scrollContainer: {
    flexGrow: 1,
    backgroundColor: '#0f1117',
    paddingHorizontal: 28,
    paddingTop: 56,
    paddingBottom: 36,
    justifyContent: 'center',
  },
  container: {
    flex: 1,
    backgroundColor: '#0f1117',
    justifyContent: 'center',
    alignItems: 'center',
  },
  topBar: {
    marginBottom: 34,
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
  },
  appName: {
    color: '#ffffff',
    fontSize: 18,
    fontWeight: '700',
  },
  appSub: {
    color: '#9ca3af',
    fontSize: 12,
    marginTop: 2,
  },
  logoutBtn: {
    borderWidth: 1,
    borderColor: '#ef4444',
    borderRadius: 8,
    paddingVertical: 6,
    paddingHorizontal: 14,
  },
  logoutText: {
    color: '#ef4444',
    fontSize: 13,
    fontWeight: '600',
  },
  title: {
    color: '#ffffff',
    fontSize: 30,
    fontWeight: '800',
    textAlign: 'center',
    marginBottom: 10,
  },
  subtitle: {
    color: '#9ca3af',
    fontSize: 14,
    textAlign: 'center',
    marginBottom: 36,
  },
  form: {
    backgroundColor: '#1a1d2e',
    borderRadius: 20,
    padding: 24,
    borderWidth: 1,
    borderColor: '#2a2f4a',
  },
  label: {
    color: '#9ca3af',
    fontSize: 13,
    fontWeight: '600',
    marginBottom: 6,
    textTransform: 'uppercase',
  },
  input: {
    backgroundColor: '#0f1117',
    color: '#ffffff',
    borderRadius: 12,
    paddingVertical: 14,
    paddingHorizontal: 16,
    fontSize: 16,
    marginBottom: 20,
    borderWidth: 1,
    borderColor: '#2a2f4a',
  },
  pickerWrapper: {
    backgroundColor: '#0f1117',
    borderRadius: 12,
    marginBottom: 20,
    borderWidth: 1,
    borderColor: '#2a2f4a',
    overflow: 'hidden',
  },
  picker: {
    color: '#ffffff',
    backgroundColor: '#0f1117',
  },
  loadingBox: {
    backgroundColor: '#0f1117',
    borderRadius: 12,
    paddingVertical: 16,
    paddingHorizontal: 16,
    marginBottom: 20,
    borderWidth: 1,
    borderColor: '#2a2f4a',
    alignItems: 'center',
  },
  loadingText: {
    color: '#9ca3af',
    marginTop: 12,
  },
  loadingTextSmall: {
    color: '#9ca3af',
    marginTop: 8,
    fontSize: 13,
  },
  noTripText: {
    color: '#ef4444',
    fontSize: 14,
    marginBottom: 20,
    textAlign: 'center',
  },
  button: {
    backgroundColor: '#4f46e5',
    borderRadius: 12,
    paddingVertical: 16,
    alignItems: 'center',
    marginTop: 8,
  },
  buttonText: {
    color: '#ffffff',
    fontSize: 16,
    fontWeight: '700',
  },
  help: {
    color: '#6b7280',
    fontSize: 12,
    textAlign: 'center',
    marginTop: 24,
  },
});