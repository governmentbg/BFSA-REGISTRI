import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { TranslateModule } from '@ngx-translate/core';

import { NomenclatureComponent } from './nomenclature.component';

describe('NomenclatureComponent', () => {
  let component: NomenclatureComponent;
  let fixture: ComponentFixture<NomenclatureComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [NomenclatureComponent],
      imports: [RouterTestingModule, TranslateModule.forRoot()],
    }).compileComponents();

    fixture = TestBed.createComponent(NomenclatureComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
