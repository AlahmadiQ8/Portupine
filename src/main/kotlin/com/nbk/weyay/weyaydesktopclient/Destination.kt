package com.nbk.weyay.weyaydesktopclient

class Destination(var host: String, var port: Int, var description: String) {
    var status: Status = Status.UNKNOWN

}

enum class Status(val text: String) {
    REACHABLE("Reachable"),
    UNREACHABLE("Unreachable"),
    UNKNOWN("Unknown"),
    LOADING("Checking")
}