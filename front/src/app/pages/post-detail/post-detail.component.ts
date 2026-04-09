import { Component, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup, FormGroupDirective } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { PostResponse, CommentResponse } from 'src/app/models/post.model';
import { PostService } from 'src/app/services/post.service';

@Component({
  selector: 'app-post-detail',
  templateUrl: './post-detail.component.html',
  styleUrls: ['./post-detail.component.scss']
})
export class PostDetailComponent implements OnInit {
  post: PostResponse | null = null;
  loading = false;
  commentForm!: FormGroup;
  submittingComment = false;
  errorMessage = '';

  @ViewChild(FormGroupDirective) formGroupDirective!: FormGroupDirective;

  constructor(
    private fb: FormBuilder,
    private postService: PostService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.commentForm = this.fb.group({
      content: ['']
    });

    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.loading = true;

    this.postService.getPost(id).subscribe({
      next: (post) => {
        this.post = post;
        this.loading = false;
      },
      error: () => {
        this.errorMessage = 'Article introuvable.';
        this.loading = false;
      }
    });
  }

  submitComment(): void {
    if (!this.post) return;
    this.submittingComment = true;

    this.postService.addComment(this.post.id, this.commentForm.value).subscribe({
      next: (comment: CommentResponse) => {
        this.post!.comments = [...this.post!.comments, comment];
        this.formGroupDirective.resetForm();
        this.submittingComment = false;
      },
      error: () => {
        this.submittingComment = false;
      }
    });
  }

  goBack(): void {
    this.router.navigate(['/']);
  }
}

