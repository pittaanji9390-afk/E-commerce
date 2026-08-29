import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { Card } from '@/components/ui/Card';
import { Button } from '@/components/ui/Button';
import { Input } from '@/components/ui/Input';

export const RegisterPage: React.FC = () => {
  const navigate = useNavigate();

  const handleRegister = (e: React.FormEvent) => {
    e.preventDefault();
    localStorage.setItem('access_token', 'mock_jwt_access_token');
    navigate('/');
  };

  return (
    <div className="max-w-md mx-auto py-12 space-y-6">
      <div className="text-center space-y-2">
        <h1 className="text-3xl font-extrabold text-slate-900">Create Account</h1>
        <p className="text-xs text-slate-500">Join the verified marketplace platform</p>
      </div>

      <Card className="p-8 space-y-5">
        <form onSubmit={handleRegister} className="space-y-4">
          <div className="grid grid-cols-2 gap-4">
            <Input label="First Name" placeholder="Jane" required />
            <Input label="Last Name" placeholder="Doe" required />
          </div>
          <Input label="Email Address" type="email" placeholder="jane@example.com" required />
          <Input label="Password" type="password" placeholder="••••••••••••" required />
          
          <div className="text-xs text-slate-500">
            By creating an account, you agree to our Terms of Service and Privacy Policy.
          </div>

          <Button type="submit" size="lg" className="w-full font-bold">
            Create Account
          </Button>
        </form>

        <div className="text-center pt-4 border-t border-slate-100 text-xs text-slate-500">
          Already registered?{' '}
          <Link to="/login" className="text-brand-600 font-bold hover:underline">
            Sign In
          </Link>
        </div>
      </Card>
    </div>
  );
};
