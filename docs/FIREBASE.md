# Firebase (Firestore) для «Движ»

## Файлы в репозитории

| Файл | Назначение |
|------|------------|
| `firebase.json` | Подключение правил и индексов для CLI |
| `.firebaserc` | Проект по умолчанию: **`dwizh-f7e85`** (как в `app/google-services.json`) |
| `firestore.rules` | Правила безопасности |
| `firestore.indexes.json` | Индекс для запроса `messages` с `orderBy(createdAt)` |

## Развёртывание

1. Установите [Firebase CLI](https://firebase.google.com/docs/cli).
2. В корне репозитория: `firebase login` (один раз).
3. Выполните:
   - только правила: `firebase deploy --only firestore:rules`
   - только индексы: `firebase deploy --only firestore:indexes`
   - правила и индексы: `firebase deploy --only firestore`

После деплоя индексов дождитесь статуса **Enabled** в консоли Firebase → Firestore → Indexes (сборка индекса может занять несколько минут).

## Что покрывают правила

- **`events` / `registrations`**: чтение событий у авторизованных; создание события только со своим `organizerId` и `participantCount == 0`; обновление полей события — только организатор; счётчик `participantCount` может менять любой авторизованный (транзакция регистрации); регистрация только в документе с id = свой `uid`.
- **`chats` / `messages`**: читать и писать сообщения могут только участники из `memberIds`; создание чата — если свой `uid` уже в `memberIds`.
- **`users/{uid}/chatList`**: владелец читает/пишет свои строки; другой участник чата может обновлять **только** денормализованные поля (`chatId`, `eventId`, `title`, `kind`, `lastPreview`, `updatedAt`) — как при отправке сообщения.

Профиль по-прежнему в документе **`users/{uid}`** (не в `chatList`): читать и менять может только владелец.

## Клиентское изменение

`EventsRepository.addEvent` теперь **всегда** пишет `organizerId` и возвращает **`Result<String>`** с id документа Firestore (вместо игнорирования id).

## Если что-то отклоняется правилами

Откройте Firebase Console → Firestore → Rules → вкладка **Rules playground**, воспроизведите путь и операцию. Частые причины: не выполнен вход, у старого события нет `organizerId` (удаление только у организатора с совпадающим полем), индекс для `messages` ещё не построен.

## App Check (обязательно для production)

В приложении включена инициализация App Check:

- `debug` сборка: `DebugAppCheckProviderFactory` (для локальной разработки).
- `release` сборка: `PlayIntegrityAppCheckProviderFactory`.

### Что включить в Firebase Console

1. Firebase Console → **Build** → **App Check**.
2. Выберите Android-приложение `com.example.authapp`.
3. Провайдер: **Play Integrity**.
4. Для Firestore/Auth/Storage постепенно переключите из **Monitor** в **Enforce** после проверки клиентских запросов.
5. Для debug-режима добавьте debug token (из логов приложения) в App Check → Debug tokens.

## Ограничения API-ключа в GCP

Ключ из `app/google-services.json` не секрет, но должен быть жёстко ограничен.

1. Google Cloud Console → **APIs & Services** → **Credentials**.
2. Откройте ключ Android-приложения Firebase.
3. В **Application restrictions** выберите **Android apps**.
4. Добавьте пакет и SHA-1/SHA-256 для всех используемых сертификатов:
   - debug keystore
   - release keystore
   - Play App Signing certificate (если публикуетесь через Google Play)
5. В **API restrictions** разрешите только нужные Firebase API для этого клиента.
6. Убедитесь, что нет unrestricted duplicate-ключей для того же проекта.
