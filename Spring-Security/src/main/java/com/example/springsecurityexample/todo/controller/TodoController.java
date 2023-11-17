package com.example.springsecurityexample.todo.controller;

import com.example.springsecurityexample.todo.Todo;
import com.example.springsecurityexample.todo.dto.TodoDto;
import com.example.springsecurityexample.todo.service.TodoService;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor //DI
@RequestMapping(value = "/api", produces = MediaTypes.HAL_JSON_VALUE)
public class TodoController {

    private final TodoService todoService;

    @PostMapping("/post/todo/{projectId}")
    public ResponseEntity<Todo> CreateTodo (@PathVariable Integer projectId, @RequestBody TodoDto todoDto){
        Todo createdTodo = todoService.CreateNewTodo(projectId, todoDto);
        return new ResponseEntity<>(createdTodo, HttpStatus.OK);
    }

    @GetMapping("/get/todo/read-all/{userId}")
    public ResponseEntity<List<Todo>> ReadAllTodo (@PathVariable Long userId){
        List<Todo> todoList = todoService.GetTodoListByUserId(userId);
        return new ResponseEntity<>(todoList, HttpStatus.OK);
    }

    @GetMapping("/get/todo/read/{todoId}")
    public ResponseEntity<Todo> ReadTodo (@PathVariable Long todoId){
        Todo todo = todoService.GetTodoByTodoId(todoId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // TODO : @PatchMapping
    // TODO : @DeleteMapping

}
