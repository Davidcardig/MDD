import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { CommentRequest, CommentResponse, PostRequest, PostResponse } from '../models/post.model';

@Injectable({
  providedIn: 'root'
})
export class PostService {
  private apiUrl = 'http://localhost:8080/api/posts';

  constructor(private http: HttpClient) {}

  /* Liste d'article auquel l'utilisateur est abonné */
  getFeed(): Observable<PostResponse[]> {
    return this.http.get<PostResponse[]>(`${this.apiUrl}/feed`);
  }

  /** Détail d'un article avec ses commentaires */
  getPost(id: number): Observable<PostResponse> {
    return this.http.get<PostResponse>(`${this.apiUrl}/${id}`);
  }

  /** Créer un article */
  createPost(request: PostRequest): Observable<PostResponse> {
    return this.http.post<PostResponse>(this.apiUrl, request);
  }

  /** Ajouter un commentaire à un article */
  addComment(postId: number, request: CommentRequest): Observable<CommentResponse> {
    return this.http.post<CommentResponse>(`${this.apiUrl}/${postId}/comments`, request);
  }
}

