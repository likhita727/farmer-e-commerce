package org.likesyou.bensalcie.pushharder

class Cart {
    var name: String? = null
    var qty: String? = null
    var price: String? = null
    var poster_id: String? = null
    var post_id: String? = null
    var time: String? = null
    var post_image: String? = null

    constructor(
        name: String?,
        qty: String?,
        price: String?,
        poster_id: String?,
        post_id: String?,
        time: String?,
        post_image: String?
    ) {
        this.name = name
        this.qty = qty
        this.price = price
        this.poster_id = poster_id
        this.post_id = post_id
        this.time = time
        this.post_image = post_image
    }

    constructor()
}