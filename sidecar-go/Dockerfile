FROM golang:1.21-bullseye as builder
WORKDIR /app
COPY ./go.mod .
RUN go env -w GO111MODULE=on \
    && go env -w GOPROXY=https://goproxy.cn,direct \
    && go mod download
COPY . .
RUN CGO_ENABLED=0 GOOS=linux GOARCH=amd64 go build -o myapp .
RUN chmod +x ./myapp

FROM debian:bullseye
COPY --from=builder /app/myapp /myapp
RUN chmod +x /myapp
ENTRYPOINT ["/myapp"]