package com.glory.company.sociedad.Models

class User(
    var id: Long = 0,
    var name: String = "",
    var nit: String = "",
    var password:String="",
    var latitude:String="",
    var longitude:String="",
    var points:Int=0,
    var dues:Int=0,
    var tel:String="",
    var address:String="",
    var type:String="",
    var store_id: Int=0,
    var avatar:String="",
    var routes: List<Route>? =null,
    var error_msg:String=""
           )