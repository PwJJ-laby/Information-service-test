<script setup>
import list from "@/data/userNav.json"
import jsCookie from "js-cookie";
</script>

<template>
  <table>
    <div class="buttons" v-for="item in list">
      <button v-if="!item.authorities || item.authorities.includes(jsCookie.get('role'))"><router-link :to="item.link" ><a>{{item.text}}</a></router-link></button>
    </div>
  </table>
</template>



<style scoped>
table {
  width: 100%;
}

.buttons {
  margin: 0.2rem;
  align-content: center;
}

.buttons button {
  background-color: rgba(240, 248, 255, 0.6);
  border: none;
  font-size: 0.9rem;
  width: 100%;
}

.buttons button:hover {
  background-color: rgba(240, 248, 255);
}

a {
  display: block;
  padding: 0.3rem;
}

</style>