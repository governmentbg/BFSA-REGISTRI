import { Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { TaskService } from 'src/app/services/task.service';
import { UserService } from 'src/app/services/user.service';

@Component({
  selector: 'app-tasks',
  templateUrl: './tasks.component.html',
  styleUrls: ['./tasks.component.scss'],
})
export class TasksComponent implements OnInit {
  tasks$: Observable<any>;
  currentUserTasks$: Observable<any>;
  userToken: any = sessionStorage.getItem('auth-user');
  userId: string;
  tasks: any;

  constructor(
    private taskService: TaskService,
    private userService: UserService
  ) {}

  ngOnInit(): void {
    if (this.userId) {
      this.userId = JSON.parse(this.userToken).userId;
    }
    this.fetchTasks();
  }

  fetchTasks() {
    this.taskService.getTasks().subscribe((res) => {
      if (res) {
        this.tasks = res.filter((el: any) => el.userId !== this.userId);
      }
    });
    this.userService.getCurrentUser().subscribe((user) => {
      if (user.id)
        this.currentUserTasks$ = this.taskService.getCurrentUserTasks(user.id);
    });
  }

  claimTask(task: any) {
    this.taskService.claimTask(task.id).subscribe({
      next: () => {
        this.fetchTasks();
      },
    });
  }

  cancelTask(task: any) {
    this.taskService.cancelTask(task.id).subscribe({
      next: () => {
        this.fetchTasks();
      },
    });
  }
}
