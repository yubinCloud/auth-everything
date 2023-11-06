FROM golang:1.20 as builder
ADD . /src
WORKDIR /src
RUN go mod download
RUN GOOS=linux go build -o app




FROM alpine:3


EXPOSE 10510
RUN apk --no-cache add ca-certificates
WORKDIR /app
COPY --from=builder /src/app .

ADD resource                $WORKDIR/
ADD ./temp/linux_amd64/main $WORKDIR/main
RUN chmod +x $WORKDIR/main


CMD ./main
