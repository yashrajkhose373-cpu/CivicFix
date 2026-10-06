/**
 * StudySync — Ultimate Student Workspace & Productivity OS
 * Full Interactive Application Engine
 */

const API_BASE = '/api';

class StudySyncApp {
    constructor() {
        this.currentTab = 'dashboard';
        this.subjects = [];
        this.tasks = [];
        this.notes = [];
        this.dashboardData = null;
        this.searchDebounceTimer = null;

        // Pomodoro State
        this.pomoMode = 'work'; // 'work', 'short', 'long'
        this.pomoDurations = { work: 25 * 60, short: 5 * 60, long: 15 * 60 };
        this.pomoTimeRemaining = this.pomoDurations.work;
        this.pomoTimerInterval = null;
        this.pomoIsRunning = false;
        this.pomoSessionsCompleted = 0;
        this.pomoMinutesCompleted = 0;
        this.selectedPomoTaskTitle = 'General Study Session';
        this.draggedTaskId = null;

        this.init();
    }

    async init() {
        console.log('Initializing StudySync Application Engine...');
        this.loadSavedTheme();
        this.setupKeyboardShortcuts();
        this.setupDynamicGreetings();
        await this.loadSubjects();
        await this.loadDashboard();
        await this.loadTasks();
        await this.loadNotes();
    }

    loadSavedTheme() {
        const savedTheme = localStorage.getItem('studysync-theme') || 'dark';
        this.applyTheme(savedTheme);
    }

    toggleTheme() {
        const isLight = document.body.classList.contains('theme-light');
        const newTheme = isLight ? 'dark' : 'light';
        this.applyTheme(newTheme);
        localStorage.setItem('studysync-theme', newTheme);
        this.showToast(`Switched to ${newTheme} mode`, 'info');
    }

    applyTheme(theme) {
        const icon = document.getElementById('theme-toggle-icon');
        if (theme === 'light') {
            document.body.classList.remove('theme-dark');
            document.body.classList.add('theme-light');
            if (icon) icon.className = 'fa-solid fa-sun text-amber';
        } else {
            document.body.classList.remove('theme-light');
            document.body.classList.add('theme-dark');
            if (icon) icon.className = 'fa-solid fa-moon';
        }
    }

    setupDynamicGreetings() {
        const hour = new Date().getHours();
        let greeting = 'Good Day!';
        let icon = 'fa-sun';
        if (hour < 12) {
            greeting = 'Good Morning, Scholar!';
            icon = 'fa-mug-hot';
        } else if (hour < 18) {
            greeting = 'Good Afternoon, Scholar!';
            icon = 'fa-sun';
        } else {
            greeting = 'Good Evening, Scholar!';
            icon = 'fa-moon';
        }
        const badge = document.getElementById('greeting-badge');
        if (badge) badge.innerHTML = `<i class="fa-solid ${icon}"></i> ${greeting}`;
    }

    setupKeyboardShortcuts() {
        window.addEventListener('keydown', (e) => {
            // Ignore if in input or textarea
            if (['INPUT', 'TEXTAREA', 'SELECT'].includes(document.activeElement.tagName)) {
                if (e.key === 'Escape') {
                    document.querySelectorAll('.modal-backdrop').forEach(m => m.classList.remove('show'));
                }
                return;
            }

            if (e.key === '/') {
                e.preventDefault();
                document.getElementById('global-search').focus();
            } else if (e.key.toLowerCase() === 'n') {
                e.preventDefault();
                this.openTaskModal('TASK');
            } else if (e.key === 'Escape') {
                document.querySelectorAll('.modal-backdrop').forEach(m => m.classList.remove('show'));
            }
        });
    }

    // --- Tab Switching ---
    switchTab(tabName) {
        this.currentTab = tabName;
        
        // Update nav items
        document.querySelectorAll('.nav-item').forEach(el => el.classList.remove('active'));
        const activeNav = document.getElementById(`nav-${tabName}`);
        if (activeNav) activeNav.classList.add('active');

        // Update sections
        document.querySelectorAll('.content-tab').forEach(el => el.classList.remove('active'));
        const activeTab = document.getElementById(`tab-${tabName}`);
        if (activeTab) activeTab.classList.add('active');

        // Update headers
        const titleMap = {
            dashboard: 'Dashboard Overview',
            tasks: 'Tasks & To-Dos',
            kanban: 'Interactive Kanban Board',
            assignments: 'Assignments & Deliverables',
            deadlines: 'Deadlines & Timeline',
            pomodoro: 'Pomodoro Focus Timer',
            notes: 'Study Notes & Cheat Sheets',
            subjects: 'Subjects & Academic Courses'
        };

        const subtitleMap = {
            dashboard: "Track your academic deadlines, tasks, notes, and study focus.",
            tasks: "Organize, prioritize, and track all your academic tasks.",
            kanban: "Visualize your workflow and drag tasks across stages.",
            assignments: "Never miss a homework, lab report, or project submission.",
            deadlines: "Track upcoming and overdue deadlines at a glance.",
            pomodoro: "Boost focus and maintain study discipline with interval timing.",
            notes: "Capture lectures, formulas, and revision notes by subject.",
            subjects: "Manage your semester course schedule and professors."
        };

        document.getElementById('page-title').textContent = titleMap[tabName] || 'StudySync';
        document.getElementById('page-subtitle').textContent = subtitleMap[tabName] || '';

        // Reload data for selected tab
        if (tabName === 'dashboard') this.loadDashboard();
        else if (tabName === 'tasks' || tabName === 'assignments' || tabName === 'deadlines' || tabName === 'kanban') this.loadTasks();
        else if (tabName === 'notes') this.loadNotes();
        else if (tabName === 'subjects') this.loadSubjects();

        // Close sidebar on mobile
        document.getElementById('sidebar').classList.remove('open');
    }

    toggleSidebar() {
        document.getElementById('sidebar').classList.toggle('open');
    }

    // --- Toast Notifications ---
    showToast(message, type = 'info') {
        const container = document.getElementById('toast-container');
        const toast = document.createElement('div');
        toast.className = `toast ${type}`;
        
        let icon = 'fa-info-circle';
        if (type === 'success') icon = 'fa-circle-check';
        if (type === 'error') icon = 'fa-triangle-exclamation';

        toast.innerHTML = `
            <i class="fa-solid ${icon}"></i>
            <span>${message}</span>
        `;
        container.appendChild(toast);

        setTimeout(() => {
            toast.style.opacity = '0';
            setTimeout(() => toast.remove(), 300);
        }, 3500);
    }

    triggerCelebration() {
        if (typeof confetti === 'function') {
            confetti({
                particleCount: 80,
                spread: 70,
                origin: { y: 0.6 }
            });
        }
    }

    // --- HTTP Helper ---
    async request(url, options = {}) {
        try {
            const res = await fetch(url, {
                headers: {
                    'Content-Type': 'application/json',
                    ...options.headers
                },
                ...options
            });

            const json = await res.json();
            if (!res.ok || !json.success) {
                const errorMsg = json.message || (json.data ? JSON.stringify(json.data) : 'An error occurred');
                throw new Error(errorMsg);
            }
            return json.data;
        } catch (err) {
            console.error('API Error:', err);
            this.showToast(err.message || 'Server connection failed', 'error');
            throw err;
        }
    }

    // --- Dashboard Operations ---
    async loadDashboard() {
        try {
            const data = await this.request(`${API_BASE}/dashboard`);
            this.dashboardData = data;
            this.renderDashboard(data);
        } catch (e) {
            console.error('Could not load dashboard data', e);
        }
    }

    renderDashboard(data) {
        // Render KPI stats
        document.getElementById('stat-total-tasks').textContent = data.totalTasks;
        document.getElementById('stat-pending-tasks').textContent = data.pendingTasks + data.inProgressTasks;
        document.getElementById('stat-completed-tasks').textContent = data.completedTasks;
        document.getElementById('stat-overdue-tasks').textContent = data.overdueTasks;
        document.getElementById('stat-completion-pct').textContent = `${data.completionRate}% completed`;
        document.getElementById('stat-progress-bar').style.width = `${data.completionRate}%`;
        document.getElementById('stat-inprogress-count').textContent = `${data.inProgressTasks} In Progress`;

        // Badges
        document.getElementById('badge-pending-tasks').textContent = data.pendingTasks;
        document.getElementById('badge-assignments').textContent = data.pendingAssignments;
        document.getElementById('badge-overdue').textContent = data.overdueTasks;

        // Upcoming Deadlines List
        const upcomingContainer = document.getElementById('dash-upcoming-list');
        if (data.upcomingDeadlines && data.upcomingDeadlines.length > 0) {
            upcomingContainer.innerHTML = data.upcomingDeadlines.map(task => `
                <div class="deadline-item">
                    <div class="deadline-left">
                        <div class="task-checkbox-custom ${task.status === 'COMPLETED' ? 'completed' : ''}" 
                             onclick="app.toggleTaskStatus(${task.id})">
                            ${task.status === 'COMPLETED' ? '<i class="fa-solid fa-check"></i>' : ''}
                        </div>
                        <div class="deadline-details">
                            <h4 style="${task.status === 'COMPLETED' ? 'text-decoration: line-through;' : ''}">${this.escapeHtml(task.title)}</h4>
                            <div class="deadline-meta">
                                <span><i class="fa-regular fa-clock"></i> ${this.formatDateTime(task.dueDate)}</span>
                                ${task.subjectName ? `<span class="badge-tag" style="background:${task.subjectColor || '#6366f1'}22; color:${task.subjectColor || '#6366f1'}">${task.subjectName}</span>` : ''}
                                <span class="badge-tag badge-priority-${task.priority}">${task.priority}</span>
                            </div>
                        </div>
                    </div>
                    <div class="deadline-right">
                        <button class="btn-icon-xs" onclick="app.editTask(${task.id})" title="Edit"><i class="fa-solid fa-pen"></i></button>
                    </div>
                </div>
            `).join('');
        } else {
            upcomingContainer.innerHTML = `<p class="text-muted" style="padding: 16px 0; text-align: center;">No upcoming deadlines. You're all caught up!</p>`;
        }

        // Pinned Notes List
        const pinnedContainer = document.getElementById('dash-pinned-notes');
        if (data.pinnedNotes && data.pinnedNotes.length > 0) {
            pinnedContainer.innerHTML = data.pinnedNotes.map(note => `
                <div class="pinned-note-item" onclick="app.editNote(${note.id})">
                    <h4>
                        <span>${this.escapeHtml(note.title)}</span>
                        <i class="fa-solid fa-thumbtack text-amber"></i>
                    </h4>
                    <p>${this.escapeHtml(note.content || 'No text content')}</p>
                    <div class="deadline-meta mt-4">
                        ${note.subjectName ? `<span class="badge-tag" style="background:${note.subjectColor || '#6366f1'}22; color:${note.subjectColor || '#6366f1'}">${note.subjectName}</span>` : ''}
                        <span>${this.formatDate(note.updatedAt)}</span>
                    </div>
                </div>
            `).join('');
        } else {
            pinnedContainer.innerHTML = `<p class="text-muted" style="padding: 16px 0; text-align: center;">No pinned notes yet. Pin notes to view them here.</p>`;
        }

        // Subject Workload Grid
        const subjectsGrid = document.getElementById('dash-subjects-grid');
        if (this.subjects && this.subjects.length > 0) {
            subjectsGrid.innerHTML = this.subjects.map(s => `
                <div class="subject-workload-card" style="border-left-color: ${s.color || '#6366f1'};">
                    <h4>${this.escapeHtml(s.name)}</h4>
                    <div class="subject-workload-code">${s.code ? s.code : 'No code'} • ${s.instructor ? s.instructor : 'Instructor'}</div>
                    <div class="subject-counts">
                        <span><i class="fa-solid fa-list-check"></i> ${s.taskCount} Tasks</span>
                        <span><i class="fa-solid fa-book-bookmark"></i> ${s.noteCount} Notes</span>
                    </div>
                </div>
            `).join('');
        } else {
            subjectsGrid.innerHTML = `<p class="text-muted">No courses added yet.</p>`;
        }
    }

    // --- Task Operations ---
    async loadTasks() {
        const search = document.getElementById('global-search').value;
        const status = document.getElementById('task-filter-status')?.value || '';
        const priority = document.getElementById('task-filter-priority')?.value || '';
        const type = document.getElementById('task-filter-type')?.value || '';
        const subjectId = document.getElementById('task-filter-subject')?.value || '';

        const params = new URLSearchParams();
        if (search) params.append('search', search);
        if (status) params.append('status', status);
        if (priority) params.append('priority', priority);
        if (type) params.append('type', type);
        if (subjectId) params.append('subjectId', subjectId);

        try {
            const tasks = await this.request(`${API_BASE}/tasks?${params.toString()}`);
            this.tasks = tasks;
            this.renderTasksTable(tasks);
            this.renderKanbanBoard(tasks);
            this.renderAssignments(tasks);
            this.renderDeadlines(tasks);
            this.populatePomoTaskOptions(tasks);
        } catch (e) {
            console.error('Could not load tasks', e);
        }
    }

    filterTasks() {
        this.loadTasks();
    }

    renderTasksTable(tasks) {
        const tbody = document.getElementById('task-table-body');
        const emptyState = document.getElementById('task-empty-state');

        if (!tasks || tasks.length === 0) {
            tbody.innerHTML = '';
            emptyState.classList.remove('hidden');
            return;
        }

        emptyState.classList.add('hidden');
        tbody.innerHTML = tasks.map(task => `
            <tr>
                <td>
                    <div class="task-checkbox-custom ${task.status === 'COMPLETED' ? 'completed' : ''}" 
                         onclick="app.toggleTaskStatus(${task.id})">
                        ${task.status === 'COMPLETED' ? '<i class="fa-solid fa-check"></i>' : ''}
                    </div>
                </td>
                <td>
                    <div class="task-title-cell ${task.status === 'COMPLETED' ? 'completed' : ''}">
                        ${this.escapeHtml(task.title)}
                    </div>
                    ${task.description ? `<div class="text-muted" style="font-size: 11px; margin-top:2px;">${this.escapeHtml(task.description)}</div>` : ''}
                </td>
                <td>
                    ${task.subjectName ? `
                        <span class="badge-tag" style="background:${task.subjectColor || '#6366f1'}22; color:${task.subjectColor || '#6366f1'}">
                            ${this.escapeHtml(task.subjectName)}
                        </span>
                    ` : '<span class="text-muted">—</span>'}
                </td>
                <td><span class="badge-tag">${task.type}</span></td>
                <td><span class="badge-tag badge-priority-${task.priority}">${task.priority}</span></td>
                <td>
                    <span style="${task.overdue ? 'color: var(--danger); font-weight: 700;' : ''}">
                        ${task.dueDate ? this.formatDateTime(task.dueDate) : '<span class="text-muted">No date</span>'}
                        ${task.overdue ? ' <i class="fa-solid fa-triangle-exclamation" title="Overdue"></i>' : ''}
                    </span>
                </td>
                <td><span class="badge-tag badge-status-${task.status}">${task.status.replace('_', ' ')}</span></td>
                <td>
                    <div class="action-btns">
                        <button class="btn-action" onclick="app.launchTaskInPomodoro('${this.escapeHtml(task.title)}')" title="Study with Focus Timer"><i class="fa-solid fa-stopwatch text-amber"></i></button>
                        <button class="btn-action" onclick="app.editTask(${task.id})" title="Edit"><i class="fa-solid fa-pen"></i></button>
                        <button class="btn-action delete" onclick="app.deleteTask(${task.id})" title="Delete"><i class="fa-solid fa-trash"></i></button>
                    </div>
                </td>
            </tr>
        `).join('');
    }

    // --- Kanban Board Implementation ---
    renderKanbanBoard(tasks) {
        const pendingCol = document.getElementById('kanban-list-PENDING');
        const inProgressCol = document.getElementById('kanban-list-IN_PROGRESS');
        const completedCol = document.getElementById('kanban-list-COMPLETED');

        if (!pendingCol || !inProgressCol || !completedCol) return;

        const pending = tasks.filter(t => t.status === 'PENDING');
        const inProgress = tasks.filter(t => t.status === 'IN_PROGRESS');
        const completed = tasks.filter(t => t.status === 'COMPLETED');

        document.getElementById('kanban-count-pending').textContent = pending.length;
        document.getElementById('kanban-count-inprogress').textContent = inProgress.length;
        document.getElementById('kanban-count-completed').textContent = completed.length;

        pendingCol.innerHTML = pending.map(t => this.renderKanbanItem(t)).join('');
        inProgressCol.innerHTML = inProgress.map(t => this.renderKanbanItem(t)).join('');
        completedCol.innerHTML = completed.map(t => this.renderKanbanItem(t)).join('');
    }

    renderKanbanItem(task) {
        return `
            <div class="kanban-item" draggable="true" ondragstart="app.dragTask(event, ${task.id})" style="border-left-color: ${task.subjectColor || '#6366f1'};">
                <div class="kanban-item-title">${this.escapeHtml(task.title)}</div>
                <div class="kanban-item-meta">
                    <span class="badge-tag badge-priority-${task.priority}">${task.priority}</span>
                    <span>${task.dueDate ? this.formatDate(task.dueDate) : 'No date'}</span>
                </div>
            </div>
        `;
    }

    dragTask(event, taskId) {
        this.draggedTaskId = taskId;
        event.dataTransfer.setData('text/plain', taskId);
    }

    allowDrop(event) {
        event.preventDefault();
    }

    async dropTask(event, newStatus) {
        event.preventDefault();
        if (!this.draggedTaskId) return;

        const task = this.tasks.find(t => t.id === this.draggedTaskId);
        if (!task || task.status === newStatus) return;

        const payload = {
            title: task.title,
            description: task.description,
            type: task.type,
            priority: task.priority,
            status: newStatus,
            subjectId: task.subjectId,
            dueDate: task.dueDate,
            estimatedMinutes: task.estimatedMinutes
        };

        try {
            await this.request(`${API_BASE}/tasks/${task.id}`, {
                method: 'PUT',
                body: JSON.stringify(payload)
            });
            this.showToast(`Task moved to ${newStatus.replace('_', ' ')}`, 'success');
            if (newStatus === 'COMPLETED') this.triggerCelebration();
            await this.loadTasks();
            await this.loadDashboard();
        } catch (e) {
            console.error('Failed to move task in Kanban', e);
        }
    }

    renderAssignments(tasks) {
        const assignmentsGrid = document.getElementById('assignments-grid');
        const emptyState = document.getElementById('assignment-empty-state');
        const assignments = tasks.filter(t => t.type === 'ASSIGNMENT' || t.type === 'PROJECT');

        if (!assignments || assignments.length === 0) {
            assignmentsGrid.innerHTML = '';
            emptyState.classList.remove('hidden');
            return;
        }

        emptyState.classList.add('hidden');
        assignmentsGrid.innerHTML = assignments.map(a => `
            <div class="assignment-card" style="border-top-color: ${a.subjectColor || '#6366f1'};">
                <div class="assignment-card-header">
                    <div>
                        <span class="badge-tag" style="background:${a.subjectColor || '#6366f1'}22; color:${a.subjectColor || '#6366f1'}; margin-bottom: 6px;">
                            ${a.subjectName || 'General'}
                        </span>
                        <h3>${this.escapeHtml(a.title)}</h3>
                    </div>
                    <span class="badge-tag badge-priority-${a.priority}">${a.priority}</span>
                </div>
                <p class="assignment-desc">${this.escapeHtml(a.description || 'No description provided.')}</p>
                <div class="assignment-footer">
                    <div>
                        <span class="text-muted"><i class="fa-regular fa-clock"></i> Due: </span>
                        <strong style="${a.overdue ? 'color: var(--danger);' : ''}">${a.dueDate ? this.formatDateTime(a.dueDate) : 'No deadline'}</strong>
                    </div>
                    <div class="action-btns">
                        <button class="btn-action" onclick="app.toggleTaskStatus(${a.id})" title="Toggle Complete">
                            <i class="fa-solid ${a.status === 'COMPLETED' ? 'fa-rotate-left' : 'fa-check'}"></i>
                        </button>
                        <button class="btn-action" onclick="app.editTask(${a.id})" title="Edit"><i class="fa-solid fa-pen"></i></button>
                        <button class="btn-action delete" onclick="app.deleteTask(${a.id})" title="Delete"><i class="fa-solid fa-trash"></i></button>
                    </div>
                </div>
            </div>
        `).join('');
    }

    renderDeadlines(tasks) {
        const overdueList = document.getElementById('deadlines-overdue-list');
        const upcomingList = document.getElementById('deadlines-upcoming-list');

        const overdue = tasks.filter(t => t.overdue && t.status !== 'COMPLETED');
        const upcoming = tasks.filter(t => !t.overdue && t.dueDate && t.status !== 'COMPLETED');

        document.getElementById('deadlines-overdue-count').textContent = overdue.length;
        document.getElementById('deadlines-upcoming-count').textContent = upcoming.length;

        if (overdue.length === 0) {
            overdueList.innerHTML = `<p class="text-muted" style="padding: 12px 0;">No overdue tasks! You are on track.</p>`;
        } else {
            overdueList.innerHTML = overdue.map(t => this.renderDeadlineCard(t, true)).join('');
        }

        if (upcoming.length === 0) {
            upcomingList.innerHTML = `<p class="text-muted" style="padding: 12px 0;">No upcoming deadlines scheduled.</p>`;
        } else {
            upcomingList.innerHTML = upcoming.map(t => this.renderDeadlineCard(t, false)).join('');
        }
    }

    renderDeadlineCard(task, isOverdue) {
        return `
            <div class="assignment-card" style="border-top-color: ${isOverdue ? 'var(--danger)' : (task.subjectColor || 'var(--secondary)')};">
                <div class="assignment-card-header">
                    <div>
                        <span class="badge-tag ${isOverdue ? 'badge-priority-URGENT' : 'badge-priority-MEDIUM'}">
                            ${isOverdue ? 'OVERDUE' : 'UPCOMING'}
                        </span>
                        <h3 class="mt-4">${this.escapeHtml(task.title)}</h3>
                    </div>
                    <span class="badge-tag badge-priority-${task.priority}">${task.priority}</span>
                </div>
                <div class="deadline-meta">
                    ${task.subjectName ? `<span><i class="fa-solid fa-book"></i> ${task.subjectName}</span>` : ''}
                    <span><i class="fa-solid fa-tag"></i> ${task.type}</span>
                </div>
                <div class="assignment-footer">
                    <div>
                        <span class="text-muted">Due: </span>
                        <strong style="${isOverdue ? 'color: var(--danger); font-weight:700;' : ''}">${this.formatDateTime(task.dueDate)}</strong>
                    </div>
                    <div class="action-btns">
                        <button class="btn-action" onclick="app.toggleTaskStatus(${task.id})" title="Mark Complete"><i class="fa-solid fa-check"></i></button>
                        <button class="btn-action" onclick="app.editTask(${task.id})"><i class="fa-solid fa-pen"></i></button>
                    </div>
                </div>
            </div>
        `;
    }

    async toggleTaskStatus(id) {
        try {
            const updated = await this.request(`${API_BASE}/tasks/${id}/toggle`, { method: 'PATCH' });
            this.showToast('Task status updated', 'success');
            if (updated.status === 'COMPLETED') {
                this.triggerCelebration();
            }
            await this.loadTasks();
            await this.loadDashboard();
            await this.loadSubjects();
        } catch (e) {
            console.error('Failed to toggle task', e);
        }
    }

    async deleteTask(id) {
        if (!confirm('Are you sure you want to delete this task?')) return;
        try {
            await this.request(`${API_BASE}/tasks/${id}`, { method: 'DELETE' });
            this.showToast('Task deleted successfully', 'success');
            await this.loadTasks();
            await this.loadDashboard();
            await this.loadSubjects();
        } catch (e) {
            console.error('Failed to delete task', e);
        }
    }

    openTaskModal(defaultType = 'TASK') {
        document.getElementById('task-id').value = '';
        document.getElementById('taskForm').reset();
        document.getElementById('task-type').value = defaultType;
        document.getElementById('task-priority').value = 'MEDIUM';
        document.getElementById('task-status').value = 'PENDING';
        document.getElementById('taskModalTitle').innerHTML = `<i class="fa-solid fa-circle-plus text-blue"></i> Create ${defaultType === 'ASSIGNMENT' ? 'Assignment' : 'Task'}`;
        this.openModal('taskModal');
    }

    async editTask(id) {
        try {
            const task = await this.request(`${API_BASE}/tasks/${id}`);
            document.getElementById('task-id').value = task.id;
            document.getElementById('task-title').value = task.title;
            document.getElementById('task-desc').value = task.description || '';
            document.getElementById('task-type').value = task.type;
            document.getElementById('task-priority').value = task.priority;
            document.getElementById('task-status').value = task.status;
            document.getElementById('task-subject').value = task.subjectId || '';
            document.getElementById('task-estimated-minutes').value = task.estimatedMinutes || '';
            
            if (task.dueDate) {
                const d = new Date(task.dueDate);
                d.setMinutes(d.getMinutes() - d.getTimezoneOffset());
                document.getElementById('task-due-date').value = d.toISOString().slice(0, 16);
            } else {
                document.getElementById('task-due-date').value = '';
            }

            document.getElementById('taskModalTitle').innerHTML = `<i class="fa-solid fa-pen-to-square text-blue"></i> Edit Task / Assignment`;
            this.openModal('taskModal');
        } catch (e) {
            console.error('Failed to load task for edit', e);
        }
    }

    async saveTask(e) {
        e.preventDefault();
        const id = document.getElementById('task-id').value;
        const payload = {
            title: document.getElementById('task-title').value,
            description: document.getElementById('task-desc').value,
            type: document.getElementById('task-type').value,
            priority: document.getElementById('task-priority').value,
            status: document.getElementById('task-status').value,
            subjectId: document.getElementById('task-subject').value ? Number(document.getElementById('task-subject').value) : null,
            estimatedMinutes: document.getElementById('task-estimated-minutes').value ? Number(document.getElementById('task-estimated-minutes').value) : null,
            dueDate: document.getElementById('task-due-date').value ? document.getElementById('task-due-date').value : null
        };

        try {
            if (id) {
                await this.request(`${API_BASE}/tasks/${id}`, {
                    method: 'PUT',
                    body: JSON.stringify(payload)
                });
                this.showToast('Task updated successfully', 'success');
            } else {
                await this.request(`${API_BASE}/tasks`, {
                    method: 'POST',
                    body: JSON.stringify(payload)
                });
                this.showToast('Task created successfully', 'success');
            }
            this.closeModal('taskModal');
            await this.loadTasks();
            await this.loadDashboard();
            await this.loadSubjects();
        } catch (err) {
            console.error('Failed to save task', err);
        }
    }

    // --- Pomodoro Focus Engine ---
    setPomoMode(mode) {
        this.pomoMode = mode;
        this.pomoTimeRemaining = this.pomoDurations[mode];
        clearInterval(this.pomoTimerInterval);
        this.pomoIsRunning = false;

        document.querySelectorAll('.pomo-mode-btn').forEach(btn => btn.classList.remove('active'));
        const btn = document.getElementById(`pomo-mode-${mode}`);
        if (btn) btn.classList.add('active');

        document.getElementById('pomo-btn-text').textContent = 'Start Focus';
        document.getElementById('pomo-icon').className = 'fa-solid fa-play';

        this.updatePomoDisplay();
    }

    togglePomoTimer() {
        if (this.pomoIsRunning) {
            clearInterval(this.pomoTimerInterval);
            this.pomoIsRunning = false;
            document.getElementById('pomo-btn-text').textContent = 'Resume Focus';
            document.getElementById('pomo-icon').className = 'fa-solid fa-play';
        } else {
            this.pomoIsRunning = true;
            document.getElementById('pomo-btn-text').textContent = 'Pause';
            document.getElementById('pomo-icon').className = 'fa-solid fa-pause';

            this.pomoTimerInterval = setInterval(() => {
                this.pomoTimeRemaining--;
                this.updatePomoDisplay();

                if (this.pomoTimeRemaining <= 0) {
                    clearInterval(this.pomoTimerInterval);
                    this.pomoIsRunning = false;
                    this.onPomoSessionFinished();
                }
            }, 1000);
        }
    }

    resetPomoTimer() {
        clearInterval(this.pomoTimerInterval);
        this.pomoIsRunning = false;
        this.pomoTimeRemaining = this.pomoDurations[this.pomoMode];
        document.getElementById('pomo-btn-text').textContent = 'Start Focus';
        document.getElementById('pomo-icon').className = 'fa-solid fa-play';
        this.updatePomoDisplay();
    }

    updatePomoDisplay() {
        const mins = Math.floor(this.pomoTimeRemaining / 60);
        const secs = this.pomoTimeRemaining % 60;
        const str = `${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`;
        
        document.getElementById('pomo-time-display').textContent = str;
        document.getElementById('pomo-nav-badge').textContent = str;
    }

    onPomoSessionFinished() {
        this.triggerCelebration();
        this.showToast(`Focus session complete! Outstanding dedication!`, 'success');
        this.pomoSessionsCompleted++;
        this.pomoMinutesCompleted += Math.round(this.pomoDurations[this.pomoMode] / 60);

        document.getElementById('pomo-today-sessions').textContent = this.pomoSessionsCompleted;
        document.getElementById('pomo-today-minutes').textContent = `${this.pomoMinutesCompleted}m`;
        document.getElementById('sidebar-pomo-count').textContent = `${this.pomoSessionsCompleted} completed`;
        document.getElementById('sidebar-pomo-bar').style.width = `${Math.min(100, this.pomoSessionsCompleted * 25)}%`;

        if (this.pomoMode === 'work') {
            this.setPomoMode('short');
        } else {
            this.setPomoMode('work');
        }
    }

    populatePomoTaskOptions(tasks) {
        const select = document.getElementById('pomo-active-task-select');
        if (!select) return;
        const pendingTasks = tasks.filter(t => t.status !== 'COMPLETED');
        select.innerHTML = '<option value="">-- General Study Session --</option>' + 
            pendingTasks.map(t => `<option value="${this.escapeHtml(t.title)}">${this.escapeHtml(t.title)}</option>`).join('');
    }

    selectPomoTask(title) {
        this.selectedPomoTaskTitle = title || 'Deep Work Session';
        document.getElementById('pomo-task-label').textContent = this.selectedPomoTaskTitle;
    }

    launchTaskInPomodoro(title) {
        this.switchTab('pomodoro');
        this.selectPomoTask(title);
        const select = document.getElementById('pomo-active-task-select');
        if (select) select.value = title;
    }

    // --- Study Notes Operations ---
    async loadNotes() {
        const search = document.getElementById('notes-search')?.value || '';
        const subjectId = document.getElementById('notes-filter-subject')?.value || '';

        const params = new URLSearchParams();
        if (search) params.append('search', search);
        if (subjectId) params.append('subjectId', subjectId);

        try {
            const notes = await this.request(`${API_BASE}/notes?${params.toString()}`);
            this.notes = notes;
            this.renderNotes(notes);
        } catch (e) {
            console.error('Could not load notes', e);
        }
    }

    filterNotes() {
        this.loadNotes();
    }

    renderNotes(notes) {
        const grid = document.getElementById('notes-grid');
        const emptyState = document.getElementById('notes-empty-state');

        if (!notes || notes.length === 0) {
            grid.innerHTML = '';
            emptyState.classList.remove('hidden');
            return;
        }

        emptyState.classList.add('hidden');
        grid.innerHTML = notes.map(note => {
            const tagsList = note.tags ? note.tags.split(',').map(t => `<span class="tag-badge">#${t.trim()}</span>`).join('') : '';
            return `
                <div class="note-card ${note.isPinned ? 'pinned' : ''}">
                    <div class="note-header">
                        <div>
                            ${note.subjectName ? `<span class="badge-tag" style="background:${note.subjectColor || '#6366f1'}22; color:${note.subjectColor || '#6366f1'}; margin-bottom: 6px;">${this.escapeHtml(note.subjectName)}</span>` : ''}
                            <h3>${this.escapeHtml(note.title)}</h3>
                        </div>
                        <button class="btn-icon-xs" onclick="app.togglePinNote(${note.id})" title="${note.isPinned ? 'Unpin note' : 'Pin note'}">
                            <i class="fa-solid fa-thumbtack ${note.isPinned ? 'text-amber' : 'text-muted'}"></i>
                        </button>
                    </div>
                    <div class="note-content-preview">
                        ${this.escapeHtml(note.content || 'No content')}
                    </div>
                    ${tagsList ? `<div class="note-tags-list">${tagsList}</div>` : ''}
                    <div class="note-footer">
                        <span>Updated: ${this.formatDate(note.updatedAt)}</span>
                        <div class="action-btns">
                            <button class="btn-action" onclick="app.editNote(${note.id})" title="Edit Note"><i class="fa-solid fa-pen"></i></button>
                            <button class="btn-action delete" onclick="app.deleteNote(${note.id})" title="Delete Note"><i class="fa-solid fa-trash"></i></button>
                        </div>
                    </div>
                </div>
            `;
        }).join('');
    }

    async togglePinNote(id) {
        try {
            await this.request(`${API_BASE}/notes/${id}/pin`, { method: 'PATCH' });
            this.showToast('Note pin status updated', 'success');
            await this.loadNotes();
            await this.loadDashboard();
        } catch (e) {
            console.error('Failed to toggle pin', e);
        }
    }

    async deleteNote(id) {
        if (!confirm('Are you sure you want to delete this study note?')) return;
        try {
            await this.request(`${API_BASE}/notes/${id}`, { method: 'DELETE' });
            this.showToast('Study note deleted', 'success');
            await this.loadNotes();
            await this.loadDashboard();
            await this.loadSubjects();
        } catch (e) {
            console.error('Failed to delete note', e);
        }
    }

    openNoteModal() {
        document.getElementById('note-id').value = '';
        document.getElementById('noteForm').reset();
        document.getElementById('noteModalTitle').innerHTML = `<i class="fa-solid fa-book-open text-amber"></i> Create Study Note`;
        this.openModal('noteModal');
    }

    async editNote(id) {
        try {
            const note = await this.request(`${API_BASE}/notes/${id}`);
            document.getElementById('note-id').value = note.id;
            document.getElementById('note-title').value = note.title;
            document.getElementById('note-content').value = note.content || '';
            document.getElementById('note-tags').value = note.tags || '';
            document.getElementById('note-subject').value = note.subjectId || '';
            document.getElementById('note-pinned').checked = !!note.isPinned;

            document.getElementById('noteModalTitle').innerHTML = `<i class="fa-solid fa-pen-to-square text-amber"></i> Edit Study Note`;
            this.openModal('noteModal');
        } catch (e) {
            console.error('Failed to load note', e);
        }
    }

    async saveNote(e) {
        e.preventDefault();
        const id = document.getElementById('note-id').value;
        const payload = {
            title: document.getElementById('note-title').value,
            content: document.getElementById('note-content').value,
            tags: document.getElementById('note-tags').value,
            subjectId: document.getElementById('note-subject').value ? Number(document.getElementById('note-subject').value) : null,
            isPinned: document.getElementById('note-pinned').checked
        };

        try {
            if (id) {
                await this.request(`${API_BASE}/notes/${id}`, {
                    method: 'PUT',
                    body: JSON.stringify(payload)
                });
                this.showToast('Note updated successfully', 'success');
            } else {
                await this.request(`${API_BASE}/notes`, {
                    method: 'POST',
                    body: JSON.stringify(payload)
                });
                this.showToast('Note created successfully', 'success');
            }
            this.closeModal('noteModal');
            await this.loadNotes();
            await this.loadDashboard();
            await this.loadSubjects();
        } catch (err) {
            console.error('Failed to save note', err);
        }
    }

    // --- Subject Operations ---
    async loadSubjects() {
        try {
            const subjects = await this.request(`${API_BASE}/subjects`);
            this.subjects = subjects;
            this.renderSubjectOptions(subjects);
            this.renderSidebarSubjects(subjects);
            this.renderSubjectsGrid(subjects);
        } catch (e) {
            console.error('Could not load subjects', e);
        }
    }

    renderSubjectOptions(subjects) {
        const taskSubjectSelect = document.getElementById('task-subject');
        const noteSubjectSelect = document.getElementById('note-subject');
        const filterTaskSubject = document.getElementById('task-filter-subject');
        const filterNoteSubject = document.getElementById('notes-filter-subject');

        const optionsHtml = subjects.map(s => `<option value="${s.id}">${this.escapeHtml(s.name)} (${s.code || 'N/A'})</option>`).join('');

        if (taskSubjectSelect) taskSubjectSelect.innerHTML = '<option value="">-- No Subject Assigned --</option>' + optionsHtml;
        if (noteSubjectSelect) noteSubjectSelect.innerHTML = '<option value="">-- No Subject Assigned --</option>' + optionsHtml;
        if (filterTaskSubject) filterTaskSubject.innerHTML = '<option value="">All Subjects</option>' + optionsHtml;
        if (filterNoteSubject) filterNoteSubject.innerHTML = '<option value="">All Subjects</option>' + optionsHtml;
    }

    renderSidebarSubjects(subjects) {
        const sidebarList = document.getElementById('sidebar-subject-list');
        if (!sidebarList) return;

        sidebarList.innerHTML = subjects.map(s => `
            <div class="subject-mini-item" onclick="app.filterBySubject(${s.id})">
                <span class="subject-color-dot" style="background-color: ${s.color || '#6366f1'}"></span>
                <span style="overflow: hidden; text-overflow: ellipsis; white-space: nowrap;">${this.escapeHtml(s.name)}</span>
            </div>
        `).join('');
    }

    renderSubjectsGrid(subjects) {
        const grid = document.getElementById('subjects-grid');
        if (!grid) return;

        grid.innerHTML = subjects.map(s => `
            <div class="subject-full-card" style="border-top: 4px solid ${s.color || '#6366f1'};">
                <div class="assignment-card-header">
                    <div>
                        <span class="badge-tag" style="background:${s.color || '#6366f1'}22; color:${s.color || '#6366f1'}; margin-bottom: 6px;">
                            ${s.code || 'NO CODE'}
                        </span>
                        <h3>${this.escapeHtml(s.name)}</h3>
                    </div>
                    <div class="action-btns">
                        <button class="btn-action" onclick="app.editSubject(${s.id})" title="Edit Subject"><i class="fa-solid fa-pen"></i></button>
                        <button class="btn-action delete" onclick="app.deleteSubject(${s.id})" title="Delete Subject"><i class="fa-solid fa-trash"></i></button>
                    </div>
                </div>
                <div class="deadline-meta">
                    <span><i class="fa-solid fa-user-tie"></i> ${s.instructor ? this.escapeHtml(s.instructor) : 'Instructor Not Specified'}</span>
                </div>
                <div class="assignment-footer">
                    <div class="subject-counts">
                        <span><i class="fa-solid fa-list-check"></i> ${s.taskCount} Tasks</span>
                        <span><i class="fa-solid fa-book-bookmark"></i> ${s.noteCount} Notes</span>
                    </div>
                    <button class="btn-outline-sm" onclick="app.filterBySubject(${s.id})">View Course</button>
                </div>
            </div>
        `).join('');
    }

    filterBySubject(subjectId) {
        this.switchTab('tasks');
        const filterSelect = document.getElementById('task-filter-subject');
        if (filterSelect) {
            filterSelect.value = subjectId;
            this.filterTasks();
        }
    }

    openSubjectModal() {
        document.getElementById('subject-id').value = '';
        document.getElementById('subjectForm').reset();
        document.getElementById('subject-color').value = '#6366f1';
        document.getElementById('subjectModalTitle').innerHTML = `<i class="fa-solid fa-shapes text-indigo"></i> Add New Subject`;
        this.openModal('subjectModal');
    }

    async editSubject(id) {
        try {
            const subject = await this.request(`${API_BASE}/subjects/${id}`);
            document.getElementById('subject-id').value = subject.id;
            document.getElementById('subject-name').value = subject.name;
            document.getElementById('subject-code').value = subject.code || '';
            document.getElementById('subject-color').value = subject.color || '#6366f1';
            document.getElementById('subject-instructor').value = subject.instructor || '';

            document.getElementById('subjectModalTitle').innerHTML = `<i class="fa-solid fa-pen-to-square text-indigo"></i> Edit Subject`;
            this.openModal('subjectModal');
        } catch (e) {
            console.error('Failed to load subject for edit', e);
        }
    }

    async saveSubject(e) {
        e.preventDefault();
        const id = document.getElementById('subject-id').value;
        const payload = {
            name: document.getElementById('subject-name').value,
            code: document.getElementById('subject-code').value,
            color: document.getElementById('subject-color').value,
            instructor: document.getElementById('subject-instructor').value
        };

        try {
            if (id) {
                await this.request(`${API_BASE}/subjects/${id}`, {
                    method: 'PUT',
                    body: JSON.stringify(payload)
                });
                this.showToast('Subject updated successfully', 'success');
            } else {
                await this.request(`${API_BASE}/subjects`, {
                    method: 'POST',
                    body: JSON.stringify(payload)
                });
                this.showToast('Subject created successfully', 'success');
            }
            this.closeModal('subjectModal');
            await this.loadSubjects();
            await this.loadDashboard();
        } catch (err) {
            console.error('Failed to save subject', err);
        }
    }

    async deleteSubject(id) {
        if (!confirm('Are you sure you want to delete this subject? Linked tasks/notes will also be affected.')) return;
        try {
            await this.request(`${API_BASE}/subjects/${id}`, { method: 'DELETE' });
            this.showToast('Subject deleted successfully', 'success');
            await this.loadSubjects();
            await this.loadDashboard();
            await this.loadTasks();
        } catch (e) {
            console.error('Failed to delete subject', e);
        }
    }

    // --- Global Search Handler ---
    handleGlobalSearch(query) {
        clearTimeout(this.searchDebounceTimer);
        this.searchDebounceTimer = setTimeout(() => {
            if (this.currentTab === 'tasks' || this.currentTab === 'assignments' || this.currentTab === 'deadlines' || this.currentTab === 'kanban') {
                this.loadTasks();
            } else if (this.currentTab === 'notes') {
                this.loadNotes();
            } else {
                this.loadTasks();
            }
        }, 300);
    }

    // --- Modal Helpers ---
    openModal(modalId) {
        document.getElementById(modalId).classList.add('show');
    }

    closeModal(modalId) {
        document.getElementById(modalId).classList.remove('show');
    }

    // --- Format Helpers ---
    formatDate(dateStr) {
        if (!dateStr) return '';
        const d = new Date(dateStr);
        return d.toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' });
    }

    formatDateTime(dateStr) {
        if (!dateStr) return '';
        const d = new Date(dateStr);
        return d.toLocaleDateString('en-US', { month: 'short', day: 'numeric', hour: '2-digit', minute: '2-digit' });
    }

    escapeHtml(text) {
        if (!text) return '';
        const div = document.createElement('div');
        div.textContent = text;
        return div.innerHTML;
    }
}

// Instantiate global application
const app = new StudySyncApp();
