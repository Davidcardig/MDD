import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { ThemesComponent } from './themes.component';
import { ThemeService } from 'src/app/services/theme.service';
import { Router } from '@angular/router';
import { of, throwError } from 'rxjs';
import { NO_ERRORS_SCHEMA } from '@angular/core';

const mockThemes = [{ id: 1, title: 'Java', subscribed: false }];

describe('ThemesComponent', () => {
  let component: ThemesComponent;
  let fixture: ComponentFixture<ThemesComponent>;
  let themeServiceMock: any;
  let routerMock: any;

  beforeEach(async () => {
    themeServiceMock = {
      getAllThemes: jest.fn().mockReturnValue(of(mockThemes)),
      subscribe: jest.fn().mockReturnValue(of({}))
    };
    routerMock = { navigate: jest.fn() };

    await TestBed.configureTestingModule({
      declarations: [ThemesComponent],
      providers: [
        { provide: ThemeService, useValue: themeServiceMock },
        { provide: Router, useValue: routerMock }
      ],
      schemas: [NO_ERRORS_SCHEMA]
    }).compileComponents();

    fixture = TestBed.createComponent(ThemesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('loadThemes charge les thèmes', () => {
    expect(component.themes).toEqual(mockThemes);
    expect(component.loading).toBe(false);
  });

  it('subscribe marque le theme comme abonné et affiche un message', () => {
    const theme = { ...mockThemes[0] } as any;
    component.subscribe(theme);
    expect(theme.subscribed).toBe(true);
    expect(component.successMessage).toContain(theme.title);
  });

  it('goBack navigue /', () => {
    component.goBack();
    expect(routerMock.navigate).toHaveBeenCalledWith(['/']);
  });
});
