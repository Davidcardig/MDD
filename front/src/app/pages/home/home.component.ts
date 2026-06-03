import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Observable } from 'rxjs';
import { AuthService } from 'src/app/services/auth.service';
import { PostService } from 'src/app/services/post.service';
import { PostResponse } from 'src/app/models/post.model';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss'],
})
export class HomeComponent implements OnInit {
  isLogged$!: Observable<boolean>;
  posts: PostResponse[] = [];
  displayedPosts: PostResponse[] = [];
  loading = false;
  sortDesc = true; // true = plus récent en premier

  constructor(
    private authService: AuthService,
    private postService: PostService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.isLogged$ = this.authService.isLogged$;
    this.authService.isLogged$.subscribe((logged) => {
      if (logged) this.loadFeed();
    });
  }

  loadFeed(): void {
    this.loading = true;
    this.postService.getFeed().subscribe({
      next: (posts) => {
        this.posts = posts;
        this.applySort();
        this.loading = false;
      },
      error: () => {
        this.loading = false;
      },
    });
  }

  toggleSort(): void {
    this.sortDesc = !this.sortDesc;
    this.applySort();
  }

   applySort(): void {
    this.displayedPosts = [...this.posts].sort((a, b) => {
      const diff =
        new Date(a.createdAt).getTime() - new Date(b.createdAt).getTime();
      return this.sortDesc ? -diff : diff;
    });
  }

  goToPost(id: number): void {
    this.router.navigate(['/posts', id]);
  }

  goToLogin(): void {
    this.router.navigate(['/login']);
  }

  goToRegister(): void {
    this.router.navigate(['/register']);
  }
}
