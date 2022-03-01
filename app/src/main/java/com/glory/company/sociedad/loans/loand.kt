package com.glory.company.sociedad.loans

import com.glory.company.sociedad.Models.User

class loand (
    var id:String="",
    var capital:Int=0,
    var date:String="",
    var due:Int=0,
    var paidout:Int=0,
    var percentage:Int=0,
    var route:String="",
    var name_route:String="",
    var total:Int=0,
    var value_pay:Int=0,
    var type:String="",
    var user:String="",
    var next:Int=0,
    var note:String="",
    var slopes:Int=0,
    var state:Boolean=false,
    var dues:ArrayList<due>? = null,
    var client: User? = null,
    var user_id:Long=0,
    var longitude:String="",
    var latitude:String="",
    var next_card:String="",
    var previous_card:String="") {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as loand

        if (id != other.id) return false
        if (capital != other.capital) return false
        if (date != other.date) return false
        if (due != other.due) return false
        if (paidout != other.paidout) return false
        if (percentage != other.percentage) return false
        if (route != other.route) return false
        if (name_route != other.name_route) return false
        if (total != other.total) return false
        if (value_pay != other.value_pay) return false
        if (type != other.type) return false
        if (user != other.user) return false
        if (next != other.next) return false
        if (note != other.note) return false
        if (slopes != other.slopes) return false
        if (state != other.state) return false
        if (dues != other.dues) return false
        if (client != other.client) return false
        if (longitude != other.longitude) return false
        if (latitude != other.latitude) return false
        if (next_card != other.next_card) return false
        if (previous_card != other.previous_card) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + capital
        result = 31 * result + date.hashCode()
        result = 31 * result + due
        result = 31 * result + paidout
        result = 31 * result + percentage
        result = 31 * result + route.hashCode()
        result = 31 * result + name_route.hashCode()
        result = 31 * result + total
        result = 31 * result + value_pay
        result = 31 * result + type.hashCode()
        result = 31 * result + user.hashCode()
        result = 31 * result + next
        result = 31 * result + note.hashCode()
        result = 31 * result + slopes
        result = 31 * result + state.hashCode()
        result = 31 * result + (dues?.hashCode() ?: 0)
        result = 31 * result + (client?.hashCode() ?: 0)
        result = 31 * result + longitude.hashCode()
        result = 31 * result + latitude.hashCode()
        result = 31 * result + next_card.hashCode()
        result = 31 * result + previous_card.hashCode()
        return result
    }
}