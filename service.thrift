namespace java tutorial2

exception InvalidOperation {
1: i32 whatOp,
2: string why
}

service Calculator {
void ping(),
oneway void commParameters(1: string message),
i32 calculate(1:i32 num1, 2:i32 num2)
}