<!-- https://vue3-lite-table.vercel.app/usage -->
<!-- TODO:
    1. User has only possibility to change own topics
    2. Change fake data to fetching from database
    3. As admin/redactor possibility to change all topics, approve or reject
-->

<template>
  <div class="properties">
    <div class="input-add">
      <label>Nowy temat:</label><input v-model="newTopicProposal" />
      <button @click="addTopic">Dodaj</button>
    </div>
    <div class="input-look">
      <label>Szukaj:</label><input v-model="searchTerm" />
    </div>
  </div>
  <div class="table-context">
    <table-lite
        :is-static-mode="true"
        :columns="table.columns"
        :rows="table.rows"
        :total="table.totalRecordCount"
        :sortable="table.sortable"
        @is-finished="tableLoadingFinish"
        @row-clicked="tableLoadingFinish"
    ></table-lite>
  </div>
</template>

<script setup>
import jsCookie from "js-cookie";
import { reactive, ref, computed } from "vue";
import TableLite from 'vue3-table-lite'

// TODO: replace with fetched data
// Fake Data for 'asc' sortable
const data = reactive([]);
for (let i = 0; i < 126; i++) {
  data.push({
      id: i,
      user: ""+i,
      topic: "TEST" + i,
      date: new Date().toDateString(),
      state: "approved",
  });
}

data.push({
  id: 127,
  user: ""+127,
  topic: "TEST" + 127,
  date: (new Date().toDateString()),
  state: "rejected",
});

const searchTerm = ref(""); // Search text
const newTopicProposal = ref(""); // user input with proposition
const maxId = ref(128); // last id assigned

// Table config
const table = reactive({
  columns: [
      {
          label: "Użytkownik",
          field: "user",
          width: "1%",
          sortable: true,
      },
      {
          label: "Temat",
          field: "topic",
          width: "5%",
          sortable: true,
          display: function (row) {
              return '<span><a href="#" class="topic" topicId="'+row.id+'">'+row.topic+'</a></span>'
          },
      },
      {
          label: "Data zaproponowania",
          field: "date",
          width: "1%",
          sortable: true,
      },
      {
          label: "Stan",
          field: "state",
          width: "1%",
          sortable: true,
          display: function (row) {
              let color = "#e8b53f";
              if (row.state == "proposed")
                  color = "#e8b53f";
              if (row.state == "approved")
                  color = "#05a32f";
              if (row.state == "rejected")
                  color = "#a31505";
              
              //make state clickable if user is admin or redactor
              if (jsCookie.get('role') == 'admin' || jsCookie.get('role') == 'redactor')
                  return '<span><a href="#" style=color:'+color+' class="state" topicId="'+row.id+'">'+row.state+'</a></span>'
              return '<span style=color:'+color+'>'+row.state+'</span>'
          },
      },
  ],
  rows: computed(() => {
      return data.filter(
      (x) =>
          x.user.toLowerCase().includes(searchTerm.value.toLowerCase()) ||
          x.topic.toLowerCase().includes(searchTerm.value.toLowerCase()) ||
          x.state.toLowerCase().includes(searchTerm.value.toLowerCase())
      );
  }),
  totalRecordCount: computed(() => {
      return table.rows.length;
  }),
  sortable: {
      order: "id",
      sort: "asc",
  },
});

function changeTopicListener(){
  let newTopic = prompt("Podaj nowy temat")

  //TODO: make request instead of local change
  if (newTopic != null && newTopic !== ''){
      let id = this.getAttribute('topicId')-1;
      data[id].topic = newTopic;
      data[id].date = new Date().toDateString();
      data[id].state = "proposed";
  }
  tableLoadingFinish()
}

function changeStateListener(){

  //TODO: make request instead of local change
  let id = this.getAttribute('topicId')-1;
  let currentState = data[id].state;

  if (currentState == 'proposed')
      data[id].state = 'approved';
  else if (currentState == 'approved')
      data[id].state = 'rejected';
  else
      data[id].state = 'proposed';


  tableLoadingFinish()
}

function addListeners(className, listenerFunction){
  let elements = document.getElementsByClassName(className)

  Array.prototype.forEach.call(elements, function (element) {
  //checking if cell already has onclick assigned
  if(element.getAttribute('listener') !== 'true'){
      element.setAttribute('listener', 'true');
      element.addEventListener("click", listenerFunction);
  }
  });

}

const tableLoadingFinish = () => {

  table.isLoading = false;
  addListeners("topic", changeTopicListener);
  if (jsCookie.get('role') == 'admin' || jsCookie.get('role') == 'redactor')
      addListeners("state", changeStateListener);

};

const addTopic = () =>{
  data.push({
          id: maxId.value++,
          user: 'user',
          topic: newTopicProposal.value,
          date: new Date().toDateString(),
          state: 'proposed',})
  newTopicProposal.value = "";
}


</script>

<style>
@import '../../assets/userLists.css';
</style>