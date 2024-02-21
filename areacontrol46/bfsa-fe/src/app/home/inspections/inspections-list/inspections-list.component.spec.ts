import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { InspectionsListComponent } from './inspections-list.component';

describe('InspectionsListComponent', () => {
  let component: InspectionsListComponent;
  let fixture: ComponentFixture<InspectionsListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [InspectionsListComponent],
      imports: [HttpClientModule, NoopAnimationsModule],
    }).compileComponents();

    fixture = TestBed.createComponent(InspectionsListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
