package com.example.springsecurityexample.todo.service;

import com.example.springsecurityexample.match.Error;
import com.example.springsecurityexample.match.Match;
import com.example.springsecurityexample.todo.Todo;
import com.example.springsecurityexample.todo.dto.TodoDto;
import com.example.springsecurityexample.todo.repository.TodoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class TodoService {
    private final TodoRepository todoRepository;

    public Todo CreateNewTodo (Integer projectId, TodoDto todoDto){
        Todo todo = Todo.builder()
                .projectId(projectId)
                .todoType(todoDto.getTodoType())
                .title(todoDto.getTitle())
                .content(todoDto.getContent())
                .build();
        Todo newTodo = todoRepository.save(todo);
        return newTodo;
    }

    public List<Todo> GetTodoListByUserId (Long userId){
        List<Todo> TodoList = todoRepository.findAllByUserId(userId);
        return TodoList;
    }

    public Todo GetTodoByTodoId (Long todoId) {
        Optional<Todo> todo = todoRepository.findById(todoId);
        if (todo.isPresent()) {
            Todo newTodo = todo.get();
            todoRepository.save(newTodo);
            return newTodo;
        } else return null;
    }


}
