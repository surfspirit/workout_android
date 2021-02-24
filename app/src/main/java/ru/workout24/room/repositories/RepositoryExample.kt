package ru.workout24.room.repositories

import android.content.Context
import ru.workout24.network.Api
import ru.workout24.room.AppDatabase
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RepositoryExample @Inject constructor(
    private val context: Context,
    private val api: Api,
    private val db: AppDatabase
) {
    /*
    private val posts = object : NetworkBoundResource<List<Post>, List<Post>>(context) {

        override fun saveCallResult(request: List<Post>) = db.exampleDao.insertPosts(request)

        override fun loadFromDb(): Flowable<List<Post>> = db.exampleDao.getAll()

        override fun createCall(): Flowable<Response<List<Post>>> = api.posts()
    }

    private val comments = object : NetworkBoundResource<List<Comment>, List<Comment>>(context) {

        override fun saveCallResult(request: List<Comment>) = db.exampleDao.insertComments(request)

        override fun loadFromDb(): Flowable<List<Comment>> = db.exampleDao.getAllComments()

        override fun createCall(): Flowable<Response<List<Comment>>> = api.comments()
    }

    val postsAndComments = object : NetworkBoundResource<List<PostWithComments>, PostsWithComments>(context) {

        override fun saveCallResult(request: PostsWithComments) {
            db.exampleDao.insertPosts(request.post)
            db.exampleDao.insertComments(request.comments)
        }

        override fun loadFromDb(): Flowable<List<PostWithComments>> = db.exampleDao.getAllPostsWithComments()

        override fun createCall(): Flowable<Response<PostsWithComments>> = Flowable.zip(
            api.posts(),
            api.comments(),
            BiFunction<Response<List<Post>>, Response<List<Comment>>, Response<PostsWithComments>>{ p, c ->

                val resp = PostsWithComments(null, null)

                if (p.isSuccessful) resp.post = p.body()
                if (c.isSuccessful) resp.comments = c.body()

                return@BiFunction Response.success(resp)
            }
        )
    }
    */
    /*
    fun postsWithComments()  = Flowable.zip(
        posts.asFlowable(),
        comments.asFlowable(),
        BiFunction<Resource<List<Post>>, Resource<List<Comment>>, List<PostsWithComments>> { posts, comments ->
            return@BiFunction if (posts.isSuccess() && comments.isSuccess()) {

                safeLet(
                    posts.getSuccessResult(),
                    comments.getSuccessResult()
                ){p, c ->
                    p.map { posts ->
                        PostsWithComments(
                            posts,
                            c.filter { it.postId == posts.id }
                        )
                    }
                } ?: run {
                    emptyList<PostsWithComments>()
                }

            } else emptyList()
        }
    )
    */
}