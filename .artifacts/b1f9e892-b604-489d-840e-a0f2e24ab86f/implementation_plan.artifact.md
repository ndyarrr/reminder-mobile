# Rencana Implementasi Tampilan Aplikasi Reminder

Membuat tampilan dasar untuk aplikasi pengingat (Reminder) menggunakan Jetpack Compose dan Material 3. Fokus utama adalah pada layar daftar pengingat dan formulir penambahan sederhana.

## Perubahan yang Diusulkan

### UI & Model Data

#### [NEW] [Reminder.kt](file:///C:/Users/LAB 1 RPL/AndroidStudioProjects/Remind/app/src/main/java/com/ndyar/remind/model/Reminder.kt)
Mendefinisikan model data sederhana untuk sebuah pengingat.

#### [NEW] [ReminderListScreen.kt](file:///C:/Users/LAB 1 RPL/AndroidStudioProjects/Remind/app/src/main/java/com/ndyar/remind/ui/screens/ReminderListScreen.kt)
Layar utama yang menampilkan daftar pengingat dengan `LazyColumn` dan tombol Floating Action Button (FAB).

#### [NEW] [ReminderItem.kt](file:///C:/Users/LAB 1 RPL/AndroidStudioProjects/Remind/app/src/main/java/com/ndyar/remind/ui/components/ReminderItem.kt)
Komponen kartu untuk menampilkan informasi ringkas dari satu pengingat.

#### [NEW] [AddReminderDialog.kt](file:///C:/Users/LAB 1 RPL/AndroidStudioProjects/Remind/app/src/main/java/com/ndyar/remind/ui/components/AddReminderDialog.kt)
Dialog untuk memasukkan judul dan waktu pengingat baru.

#### [MODIFY] [MainActivity.kt](file:///C:/Users/LAB 1 RPL/AndroidStudioProjects/Remind/app/src/main/java/com/ndyar/remind/MainActivity.kt)
Memperbarui `MainActivity` untuk menampilkan `ReminderListScreen`.

## Rencana Verifikasi

### Verifikasi Manual
- Menjalankan aplikasi di emulator atau perangkat fisik.
- Memastikan daftar pengingat (dengan data mock) muncul.
- Memastikan dialog tambah pengingat terbuka saat FAB diklik.
