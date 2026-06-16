# REST Data Client

Client Java thuan cho 2 bai REST:

- `src/main/java/DataRestClient.java`: bai tinh tong mang so nguyen.
- `src/main/java/CharacterRestClient.java`: bai sap xep cac tu trong chuoi.

## Thong tin mac dinh

- Student code: `B22DCDT074`
- Data qCode: `eTF6h0kP`
- Character qCode: `vDWuPkz8`
- Exam server: `36.50.135.242`

## Yeu cau

- Java 11 tro len
- Khong can Maven/Gradle hay thu vien ngoai

## Chay test

```bat
test.bat
```

## Chay nop bai

Bai data:

```bat
run.bat data
```

Bai character:

```bat
run.bat character
```

Hoac khong dung file `.bat`:

```bat
java src\main\java\DataRestClient.java
java src\main\java\CharacterRestClient.java
```

Hoac truyen tham so:

```bat
run.bat 36.50.135.242 B22DCDT074 eTF6h0kP
```

Chuong trinh se:

1. Gui `GET /api/rest/data?studentCode=B22DCDT074&qCode=eTF6h0kP`
2. Doc `requestId` va mang so nguyen `data`
3. Tinh tong cac so trong `data`
4. Gui `POST /api/rest/data/submit` voi JSON gom `studentCode`, `qCode`, `requestId`, `answer`
