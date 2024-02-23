export interface R<T> {
    code: number,
    data: T,
    msg: string
}

export function success<T>(data: T): R<T> {
    return {
        data,
        code: 0,
        msg: 'success'
    }
}

export function badRequest<T>(data: T, msg: string): R<T> {
    return {
        data,
        code: -1,
        msg
    }
}