import { ComponentFixture, TestBed } from '@angular/core/testing';
import { CreatePostComponent } from './create-post.component';
import { ReactiveFormsModule } from '@angular/forms';
import { PostService } from 'src/app/services/post.service';
import { ThemeService } from 'src/app/services/theme.service';
import { Router } from '@angular/router';
import { of, throwError } from 'rxjs';
import { NO_ERRORS_SCHEMA } from '@angular/core';

describe('CreatePostComponent', () => {
  let component: CreatePostComponent;
  let fixture: ComponentFixture<CreatePostComponent>;
  let postServiceMock: any;
  let themeServiceMock: any;
  let routerMock: any;

  beforeEach(async () => {
    postServiceMock = { createPost: jest.fn().mockReturnValue(of({ id: 1 })) };
    themeServiceMock = { getSubscribedThemes: jest.fn().mockReturnValue(of([])) };
    routerMock = { navigate: jest.fn() };

    await TestBed.configureTestingModule({
      declarations: [CreatePostComponent],
      imports: [ReactiveFormsModule],
      providers: [
        { provide: PostService, useValue: postServiceMock },
        { provide: ThemeService, useValue: themeServiceMock },
        { provide: Router, useValue: routerMock }
      ],
      schemas: [NO_ERRORS_SCHEMA]
    }).compileComponents();

    fixture = TestBed.createComponent(CreatePostComponent);
    component = fixture.componentInstance;
    // Ne pas appeler fixture.detectChanges() pour éviter les erreurs liées aux
    // form controls custom (mat-select, etc.). Appelons manuellement ngOnInit().
    component.ngOnInit();
  });

  it('ngOnInit charge les themes', () => {
    expect(component.loading).toBe(false);
    expect(component.subscribedThemes).toEqual([]);
  });

  it('onSubmit ne soumet pas si le formulaire invalide', () => {
    component.postForm.get('title')?.setValue('');
    component.postForm.get('content')?.setValue('');
    component.postForm.get('themeId')?.setValue(null);
    component.onSubmit();
    expect(postServiceMock.createPost).not.toHaveBeenCalled();
  });

  it('onSubmit crée le post et navigue', () => {
    component.postForm.get('title')?.setValue('Titre');
    component.postForm.get('content')?.setValue('Contenu');
    component.postForm.get('themeId')?.setValue(1);
    component.onSubmit();
    expect(postServiceMock.createPost).toHaveBeenCalled();
    expect(routerMock.navigate).toHaveBeenCalledWith(['/posts', 1]);
  });
});


