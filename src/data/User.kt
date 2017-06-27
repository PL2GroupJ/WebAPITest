package data

data class User(
        val accessToken: String,
        val accessTokenSecret: String,
        val userId: String,
        val screenName: String
) {
    companion object {
        lateinit var user: User
    }
}