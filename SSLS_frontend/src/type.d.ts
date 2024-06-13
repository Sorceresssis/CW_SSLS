type Result = {
    code: number,
    msg: string,
    data: any
}


type category = {
    id: number,
    name: string,
}

type bookProfile = {
    id: number,
    name: string,
    authors?: string,
    imageUrl?: string,
    status: string
}

type BorrowResult = {
    code: number,
    bookId: number,
    bookName: string,
    authors: string,
    borrowDate?: string,
    dueDate?: string,
}

type BorrowInfo = {
    id: number,
    bookId: number,
    bookName: string,
    authors: string,
    imageUrl: string
    borrowDate: string,
    dueDate: string,
    renew: string,
}

type BorrowHistoryInfo = {
    id: number,
    bookId: number,
    bookName: string,
    authors: string,
    imageUrl: string
    borrowDate: string,
    returnDate: string,
}

type FineInfo = {
    id: number,
    amount: number,
    date?: string,
    status: number,
    borrowDate: string,
    dueDate: string,
    returnDate: string,
    overdue: string,
    bookName: string,
    authors: string,
    imageUrl: string
}