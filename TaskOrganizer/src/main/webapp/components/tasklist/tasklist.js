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
	shadow;
	numberOfTasks;
	table;
	taskListDiv;
	tbody;
	allstatuses;
	constructor() {
		super();
		this.shadow = this.attachShadow({ mode: "open" });
		this.table = document.importNode(tasktable.content, true);
		const content = template.content.cloneNode(true);
		this.shadow.appendChild(content);
		this.taskListDiv = this.shadow.querySelector("#tasklist");
		this.tbody = this.table.querySelector("tbody");
		this.numberOfTasks = 0;
		this.allstatuses = [];
	}

	/**
	 * @public
	 * @param {Array} list with all possible task statuses
	 */
	setStatuseslist(allstatuses) {
		this.allstatuses = allstatuses;
	}

	/**
	 * Add callback to run on change on change of status of a task, i.e. on change in the SELECT element
	 * @public
	 * @param {function} callback
	 */
	changestatusCallback(callback) {
		/**
		 * Fill inn rest of code
		 */
	}

	/**
	 * Add callback to run on click on delete button of a task
	 * @public
	 * @param {function} callback
	 */
	deletetaskCallback(callback) {
		/**
		 * Fill inn rest of code
		 */
	}

	/**
	 * Add task at top in list of tasks in the view
	 * @public
	 * @param {Object} task - Object representing a task
	 */
	showTask(task) {

		this.numberOfTasks++;
		const newRow = document.importNode(taskrow.content, true);
		const tr = newRow.querySelector("tr");
		const btn = newRow.querySelector("button[type='button']");
		const select = newRow.querySelector("select");

		//here add something that updates all statuses
		this.allstatuses.forEach(status => {
			const option = document.createElement("option");
			option.value = status;
			option.innerText = status;


			select.appendChild(option);
		})
		//change status listener
		select.addEventListener("change", (e) => {
			const newStatus = e.target.value;
			if(newStatus === "0"){
				return;
			} 
			const updatedTask = { id: task.id, status: newStatus };
			
			this.updateTask(updatedTask);
		})
		//sets task-id to the row
		tr.setAttribute("task-id", task.id);
		newRow.querySelector("td:first-child").textContent = task.title;
		newRow.querySelector("td:nth-child(2)").textContent = task.status;

		//add remove task to the remove button
		btn.addEventListener("click", () => {
			this.removeTask(task.id);
		})

		this.tbody.appendChild(newRow);

		this.taskListDiv.appendChild(this.table);
	}

	/**
	 * Update the status of a task in the view
	 * @param {Object} task - Object with attributes {'id':taskId,'status':newStatus}
	 */
	updateTask(task) {
		console.log(task);
	}

	/**
	 * Remove a task from the view
	 * @param {Integer} task - ID of task to remove
	 */
	removeTask(id) {
		console.log(id);
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
