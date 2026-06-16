# REST Data Client

Client Java thuan de goi REST DataService, tinh tong mang `data`, roi submit dap an.

## Thong tin mac dinh

- Student code: `B22DCDT074`
- qCode: `eTF6h0kP`
- Exam server: `36.50.135.242`

## Yeu cau

- Java 11 tro len
- Khong can Maven/Gradle hay thu vien ngoai

## Chay test

```bat
test.bat
```

## Chay nop bai

Dung gia tri mac dinh:

```bat
run.bat
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
