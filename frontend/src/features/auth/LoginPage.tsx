import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { Card } from '@/components/ui/Card';
import { Button } from '@/components/ui/Button';
import { Input } from '@/components/ui/Input';

export const LoginPage: React.FC = () => {
  const navigate = useNavigate();

  const handleLogin = (e: React.FormEvent) => {
    e.preventDefault();
    localStorage.setItem('access_token', 'mock_jwt_access_token');
    navigate('/');
  };

  return (
    <div className="max-w-md mx-auto py-12 space-y-6">
      <div className="text-center space-y-2">
        <h1 className="text-3xl font-extrabold text-slate-900">Welcome Back</h1>
        <p className="text-xs text-slate-500">Sign in to your customer, merchant, or admin account</p>
      </div>

      <Card className="p-8 space-y-5">
        <form onSubmit={handleLogin} className="space-y-4">
          <Input
            label="Email Address"
            type="email"
            placeholder="you@example.com"
            defaultValue="customer@example.com"
            required
          />
          <Input
            label="Password"
            type="password"
            placeholder="••••••••••••"
            defaultValue="Password123!"
            required
          />

          <div className="flex items-center justify-between text-xs">
            <label className="flex items-center gap-2 cursor-pointer">
              <input type="checkbox" defaultChecked className="accent-brand-600 rounded" />
              <span>Remember me</span>
            </label>
            <Link to="/forgot-password" className="text-brand-600 hover:underline font-semibold">
              Forgot password?
            </Link>
          </div>

          <Button type="submit" size="lg" className="w-full font-bold">
            Sign In
          </Button>
        </form>

        <div className="text-center pt-4 border-t border-slate-100 text-xs text-slate-500">
          Don't have an account?{' '}
          <Link to="/register" className="text-brand-600 font-bold hover:underline">
            Create account
          </Link>
        </div>
      </Card>
    </div>
  );
};
