const template = document.createElement("template");
template.innerHTML = `
    <link rel="stylesheet" type="text/css" href="${import.meta.url.match(/.*\//)[0]}/tasklist.css"/>

    <div id="tasklist"></div>`;

const tasktable = document.createElement("template");
tasktable.innerHTML = `
    <table>
        <thead><tr><th>Task</th><th>Status</th></tr></thead>
        <tbody></tbody>
    </table>`;

const taskrow = document.createElement("template");
taskrow.innerHTML = `
    <tr>
        <td></td>
        <td></td>
        <td>
            <select>
                <option value="0" selected>&lt;Modify&gt;</option>
            </select>
        </td>
        <td><button type="button">Remove</button></td>
    </tr>`;

/**
  * TaskList
  * Manage view with list of tasks
  */
class TaskList extends HTMLElement {
	#updateCallback;
	#removeCallback;
	//list of all statuses
	allStatuses = [];
	//div with id="tasklist", container
	#taskListDiv;
	//tbody for row injection(tasks)
	#tbody;
	//amout of tasks
	#taskCount = 0;
	constructor() {
		super();
		//attach the shadow dom
		const shadowRoot = this.attachShadow({ mode: 'open' });

		// Clone the template content and append it to the shadowRoot
		const templateContent = template.content.cloneNode(true);
		shadowRoot.appendChild(templateContent);

		// Initialize task list container from the shadowRoot
		this.#taskListDiv = shadowRoot.querySelector("#tasklist");

		// Clone the tasktable template content, find the table and tbody, and perform initial setup
		const tableClone = tasktable.content.cloneNode(true);
		const table = tableClone.querySelector("table");
		this.#tbody = table.querySelector("tbody");

		// Initially hide the table as there are no tasks
		table.style.display = 'none';

		// Append the table to the task list container
		this.#taskListDiv.appendChild(table);
	}


	#toggleTable() {
		const table = this.#taskListDiv.querySelector("table");
		if (table) {
			if (this.getNumtasks() === 0) {
				table.style.display = 'none';
			} else {
				table.style.display = 'table';
			}
		}
	}
	/**
	 * @public
	 * @param {Array} list with all possible task statuses
	 */
	setStatuseslist(allstatuses) {
		this.allStatuses = allstatuses;
	}

	/**
	 * Add callback to run on change on change of status of a task, i.e. on change in the SELECT element
	 * @public
	 * @param {function} callback
	 */
	changestatusCallback(callback) {
		this.#updateCallback = callback;
	}

	/**
	 * Add callback to run on click on delete button of a task
	 * @public
	 * @param {function} callback
	 */
	deletetaskCallback(callback) {
		this.#removeCallback = callback;
	}

	/**
	 * Add task at top in list of tasks in the view
	 * @public
	 * @param {Object} task - Object representing a task
	 */
	showTask(task) {
		this.#taskCount++;

		const newRow = document.importNode(taskrow.content, true).querySelector("tr");
		newRow.setAttribute("task-id", task.id);
		newRow.querySelector("td:first-child").textContent = task.title;
		newRow.querySelector("td:nth-child(2)").textContent = task.status;
		//
		// status
		//
		const select = newRow.querySelector("select");
		const originalStatus = select.querySelector("option").value;
		this.allStatuses.forEach((status) => {
			const option = document.createElement("option");
			option.textContent = status;
			option.value = status;
			select.appendChild(option);
		});
		//add event listener on select
		select.addEventListener("change", () => {
			const newStatus = select.value;
			console.log(newStatus);
			if (newStatus !== originalStatus) {
				if (this.#updateCallback && confirm(`set '${task.title}' to ${newStatus}`)) {
					this.#updateCallback(task.id, newStatus)
					this.updateTask({ ...task, status: newStatus })
				}
			}
			select.value = originalStatus
		});
		//
		// remove button
		//
		const button = newRow.querySelector("button");
		button.addEventListener("click", () => {
			if (this.deletetaskCallback && confirm(`delete task '${task.title}'?`)) {
				this.#removeCallback(task.id);
				console.log(this.#removeCallback(task.id));
				this.removeTask(task.id);
			}
		});
		//reversing the order of tasks :)
		this.#tbody.insertBefore(newRow, this.#tbody.firstChild);
		this.#toggleTable();
	}

	/**
	 * Update the status of a task in the view
	 * @param {Object} task - Object with attributes {'id':taskId,'status':newStatus}
	 */
	updateTask(task) {
		const taskElement = this.#taskListDiv.querySelector(`tr[task-id="${task.id}"]`);
		if (taskElement) {
			taskElement.querySelector("td:nth-child(2)").textContent = task.status;
		} else {
			console.error(`Task with ${task.id} not found.`);
		}
	}

	/**
	 * Remove a task from the view
	 * @param {Integer} task - ID of task to remove
	 */
	removeTask(id) {
		const taskElement = this.#taskListDiv.querySelector(`tr[task-id="${id}"]`);
		if (taskElement) {
			this.#tbody.removeChild(taskElement);
			this.#taskCount--;
			this.#toggleTable();
		} else {
			console.error(`Task with ID ${id} not found.`);
		}
	}

	/**
	 * @public
	 * @return {Number} - Number of tasks on display in view
	 */
	getNumtasks() {
		return this.numberOfTasks;
	}
}
customElements.define('task-list', TaskList);
