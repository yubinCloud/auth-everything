FROM golang:1.18-bullseye as builder
ADD . /src
WORKDIR /src
RUN go env -w GO111MODULE=on \
    && go env -w GOPROXY=https://goproxy.cn,direct \
    && go mod download
RUN GOOS=linux GOARCH=amd64 go build -o app
RUN chmod +x ./app


FROM debian:bullseye
EXPOSE 10510
WORKDIR /app
COPY --from=builder /src/app .
RUN chmod +x ./app
CMD ./app
