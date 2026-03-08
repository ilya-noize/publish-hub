# publish-hub

Content Creation Platform

|     | URL                                                          | description                | method | Controller              |        |
|-----|--------------------------------------------------------------|----------------------------|--------|-------------------------|--------|
| [_] | /users                                                       |                            |        |                         |        |
| [_] | /users/{userId}                                              |                            |        |                         |        |
| [x] | /users/{userId}/posts                                        | createPostWithComments     | POST   | UserContentController   | AUTH   |
| [x] | /users/{userId}/posts                                        | getPostsByAuthor           | GET    | UserStatisticController |        |
| [_] | /users/{userId}/posts/{postId}                               |                            |        |                         |        |
| [x] | /users/{userId}/posts/{postId}/comments                      | addCommentToPost           | POST   | UserCommentController   | AUTH   |
| [x] | /users/{userId}/posts/{postId}/comments                      | getUsersCommentsInPost     | GET    | UserCommentController   |        |
| [x] | /users/{userId}/posts/{postId}/comments                      | deletePostComments         | DELETE | UserCommentController   | AUTH   |
| [_] | /users/{userId}/posts/{postId}/comments/{commentsId}         |                            |        |                         |        |
| [x] | /users/{userId}/posts/{postId}/comments/{commentsId}         | getPostCommentsById        | GET    | UserCommentController   |        |
| [x] | /users/{userId}/posts/{postId}/comments/{commentsId}         | deletePostCommentsById     | DELETE | UserCommentController   | AUTH   |
| [x] | /users/{userId}/posts/{postId}/comments/{commentsId}/approve | approveComment             | PATCH  | UserCommentController   | AUTH   |
| [x] | /users/{userId}/posts/{postId}/comments/{commentsId}/reject  | rejectComment              | PATCH  | UserCommentController   | AUTH   |
| [x] | /users/{userId}/comments                                     | getAllUserComments         | GET    | UserStatisticController |        |
| [_] | /users/{userId}/comments/{commentsId}                        |                            |        |                         |        |
| [x] | /users/{userId}/recent                                       | getRecentPostsWithComments | GET    | UserStatisticController |        |
| [x] | /users/{userId}/activity                                     | getUserActivity            | GET    | UserStatisticController |        |
| [x] | /posts                                                       | getAllPosts                | GET    | PostController          | PUBLIC |
| [x] | /posts/{postId}                                              | getById                    | GET    | PostController          | PUBLIC |
| [x] | /posts/{postId}/comments                                     | getPostComments            | GET    | PostController          | PUBLIC |
| [x] | /posts/{postId}/comments/{commentsId}                        | getPostCommentById         | GET    | PostController          | PUBLIC |
| [_] | /posts/tags/                                                 | getPostsByAllTags          | GET    | PostController          | PUBLIC |
| [x] | /posts/tags/{tagId}                                          | getPostsByTagWithDetails   | GET    | PostController          | PUBLIC |
| [_] |                                                              |                            |        |                         |        |

